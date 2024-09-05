package com.xaniapp.xani.business;

import android.content.Context;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpHeaders;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.CloseableHttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.entity.StringEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder;
import com.google.gson.GsonBuilder;
import com.xaniapp.xani.helpers.CallbackInterface;
import com.xaniapp.xani.helpers.DateDeserializer;
import com.xaniapp.xani.entites.Result;
import com.xaniapp.xani.entites.api.AuthenticateMessage;
import com.xaniapp.xani.entites.api.AuthenticateResponse;
import com.xaniapp.xani.entites.api.FeedResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

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

    public static void aquireFeed(Context context, CallbackInterface callback) {

        aquireFeedAsync(context, result -> {

            var feedResult = new Result<Integer>();
            var mergeResult = DatabaseBusiness.mergeFeedFromApi(context, (FeedResponse)result.data);

            if (!mergeResult.success) {
                feedResult.setFail(mergeResult.errorMessage);
            }

            callback.onTaskFinished(result);
        });
    }

    public static void aquireFeedAsync(Context context, final CallbackInterface callback) {

        final var apiThread = new Thread(() -> {

            var result = new Result<FeedResponse>();
            var authenticated = checkAuthenticated(context);
            if (!authenticated.success) {
                result.setFail(authenticated.errorMessage);
            }
            else {

                var feedResponse = sendToAPI(ApiAction.GET, String.format("feed?u_id=%s", Authorisation.id), Authorisation.token, null, FeedResponse.class);
                if (!feedResponse.success) {
                    result.setFail(feedResponse.errorMessage);
                }
                else {
                    result.data = (FeedResponse) feedResponse.data;
                }
            }

            callback.onTaskFinished(result);
        });
        apiThread.start();
    }

    public static Result<Integer> checkAuthenticated(Context context) {

        final var result = new Result<Integer>();

        if (Authorisation.token == null) {
            final var authenticateMessage = new AuthenticateMessage();
            final var datastore = DatastoreBusiness.getInstance(context);

            authenticateMessage.username = datastore.getStringValue(DatastoreBusiness.Key.USERNAME);
            authenticateMessage.password_hash = datastore.getStringValue(DatastoreBusiness.Key.HASH);

            var authenticateResponse = sendToAPI(ApiAction.POST, "authorisation", null, authenticateMessage, AuthenticateResponse.class);
            if (authenticateResponse.success) {
                final var authData = (AuthenticateResponse) authenticateResponse.data;
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

        final var gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        final var gson = gsonBuilder.create();
        final var httpClient = HttpClientBuilder.create().build();
        final var result = new Result<T1>();

        try {
            /* encode the message */
            final var messageJson = message == null ? null : gson.toJson(message, message.getClass());

            /* create and execute the request */
            CloseableHttpResponse response = null;

            switch(action) {

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

  //  public interface MyCallbackInterface{
   //     void onDownloadFinished(Result<?> result);
  //  }
}
