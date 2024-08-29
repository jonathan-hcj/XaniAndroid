package com.xaniapp.xani.business;

import androidx.annotation.NonNull;

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

public class AuthenticateBusiness {

    private enum ApiAction {
        GET,
        POST
    }

    //http://192.168.0.18/Xani/api/feed?u_id=2
    private final static String baseUrl = "http://192.168.0.18/Xani/api";
    // private final static String baseUrl = "http://192.168.0.18:5138/api";

    public static void processLogin() {
        processAuthenticate();
    }

    public static <T1> Result<?> sendToAPI(ApiAction action, String controller, String token, Object message, Class<?> classOfResponse) {

        var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        var gson = gsonBuilder.create();

        var sb = new StringBuilder();
        var httpClient = HttpClientBuilder.create().build();//
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
                    postRequest.setEntity(new StringEntity(messageJson));

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
        }
        catch (Exception e1) {
            result.setFail(e1.getMessage());
        }

        return result;
    }

    public static Result<String> ReadHttpResponse (CloseableHttpResponse response) {

        var result = new Result<String>();
        var sb = new StringBuilder();
        var responseEntity = response.getEntity();

        var statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 401) {
            result.setFail("Unauthorised");
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

    @NonNull
    public static Result processAuthenticate() {
        var result = new Result<Integer>();

        var apiThread = new Thread(() -> {

            var authenticateMessage = new AuthenticateMessage();
            authenticateMessage.u_id = 2;
            authenticateMessage.u_password_hash = "a";

            var authenticateResponse = sendToAPI(ApiAction.POST, "authorisation", null, authenticateMessage, AuthenticateResponse.class);
            if (authenticateResponse.success) {

                var authenticate = (AuthenticateResponse) authenticateResponse.data;
                var feedResponse = sendToAPI(ApiAction.GET, String.format("feed?u_id=%s", 2), authenticate.u_token, null, FeedResponse.class);
                //   var feedResponse = sendToAPI(ApiAction.GET, String.format("feed?u_id=%s", 2), null, null, FeedResponse.class);
                if (feedResponse.success) {

                    var feed = (FeedResponse) feedResponse.data;
                    for (var item : feed.f_items) {

                    }

                }
            }
        });
        apiThread.start();
        return result;
    }
}
