package com.xaniapp.xani.business;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpHeaders;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.CloseableHttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;
import com.google.gson.GsonBuilder;
import com.xaniapp.xani.DateDeserializer;
import com.xaniapp.xani.entites.Result;
import com.xaniapp.xani.entites.api.AuthenticateMessage;
import com.xaniapp.xani.entites.api.AuthenticateResponse;
import com.xaniapp.xani.entites.api.FeedResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.prefs.Preferences;

public class ApiBusiness {

    /*
        "http://192.168.0.18/Xani/api"
        "http://192.168.0.18:5138/api"
    */
    private static final String baseUrl = "http://192.168.0.18/Xani/api";
    private enum ApiAction {
        GET,
        POST
    }

    public static void aquireFeed() {

        aquireFeedAsync(result -> {

            var b = (FeedResponse) result.data;
            // Do something when download finished
        });
    }

    public static void aquireFeedAsync(final MyCallbackInterface callback) {

        var apiThread = new Thread(() -> {

            var result = new Result<FeedResponse>();
            var authenticated = checkAuthenticated();
            if (!authenticated.success) {
                result.setFail(authenticated.errorMessage);
            }
            else {

                var feedResponse = sendToAPI(ApiAction.GET, String.format("feed?u_id=%s", Authorisation.id), Authorisation.token, null, FeedResponse.class);
                if (feedResponse.success) {

                    var feed = (FeedResponse) feedResponse.data;
                    for (var item : feed.f_items) {

                    }
                    result.data = feed;
                }
            }

            callback.onDownloadFinished(result);
        });
        apiThread.start();
    }

    public static Result<Integer> checkAuthenticated() {

        var result = new Result<Integer>();

        if (Authorisation.token == null) {
            var authenticateMessage = new AuthenticateMessage();

            authenticateMessage.username = "Death";
            authenticateMessage.password_hash = "37023dfa13e7c584c259d5e383ff88c1f25e2b45403ecd5fe581132e7eb5c6ed";

            var authenticateResponse = sendToAPI(ApiAction.POST, "authorisation", null, authenticateMessage, AuthenticateResponse.class);
            if (authenticateResponse.success) {
                var authData = (AuthenticateResponse) authenticateResponse.data;
                Authorisation.id = authData.id;
                Authorisation.token = authData.token;
            }
            else {
                result.setFail(authenticateResponse.errorMessage);
            }
        }

        return result;
    }

    //region "NET"

    private static <T1> Result<?> sendToAPI(ApiAction action, String controller, String token, Object message, Class<?> classOfResponse) {

        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        var gson = gsonBuilder.create();
        var httpClient = HttpClientBuilder.create().build();
        var result = new Result<T1>();

        try {
            /* encode the message */
            var messageJson = message == null ? null : gson.toJson(message, message.getClass());

            /* create an execute the post */
            CloseableHttpResponse response = null;

            switch(action)
            {
                case GET:
                    var getRequest = new HttpGet(String.format("%s/%s", baseUrl, controller));
                    if (token != null) {
                        getRequest.setHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
                    }
                    response = httpClient.execute(getRequest);
                    break;

                case POST:
                    var postRequest = new HttpPost(String.format("%s/%s", baseUrl, controller));
                    postRequest.setHeader(HttpHeaders.ACCEPT, "application/json");
                    postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

                    if (token != null) {
                        postRequest.setHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
                    }
                    if (messageJson != null) {
                        postRequest.setEntity(new StringEntity(messageJson));
                    }

                    response = httpClient.execute(postRequest);
                    break;
            }


            if (response == null) {
                result.setFail("No response returned from the data service");
            }
            else {
                var parsedPostResponse = ReadHttpResponse (response);
                if (!parsedPostResponse.success) {
                    result.setFail(parsedPostResponse.errorMessage);
                }
                else {
                    result.data = (T1) gson.fromJson(parsedPostResponse.data, classOfResponse);
                }
            }

            httpClient.close();
        }
        catch (Exception e1) {
            result.setFail(e1.getMessage());
        }

        return result;
    }

    private static Result<String> ReadHttpResponse (CloseableHttpResponse response) {

        var result = new Result<String>();
        var sb = new StringBuilder();
        var responseEntity = response.getEntity();

        var statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 401) {
            result.setFail("Unauthorised");
        }
        else if (statusCode == 404){
            result.setFail("Not found");
        }
        else {

            try {
                if (responseEntity != null) {

                    var bufferedReader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
                    var line = "";
                    while((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    bufferedReader.close();

                }
                result.data = sb.toString();
            }
            catch (Exception e) {
                result.setFail(e.getMessage());
            }
        }
        return result;
    }
    //endregion

    private static class Authorisation {
        public static int id;
        public static String token;
    }

    public interface MyCallbackInterface{
        void onDownloadFinished(Result<?> result);
    }
}
