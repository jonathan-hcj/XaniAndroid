package com.xaniapp.xani.business;

import android.content.Context;

import com.xaniapp.xani.dataaccess.AppDatabase;
import com.xaniapp.xani.entites.Result;
import com.xaniapp.xani.entites.api.FeedResponse;
import com.xaniapp.xani.entites.da.Post;
import com.xaniapp.xani.entites.da.User;

import java.util.ArrayList;

public class DatabaseBusiness {

    public static Result<Integer> mergeFeedFromApi(Context context, FeedResponse feed) {

        var appDatabase = AppDatabase.getDatabase(context);
        var userDao = appDatabase.userDao();
        var postDao = appDatabase.postDao();
        var result = new Result<Integer>();

        if (feed == null) {
            result.setFail("No feed has been provided for merge");
        }
        else {
            var incomingPosts = new ArrayList<Post>();
            var incomingUsers = new ArrayList<User>();

            for (var item : feed.f_post_items) {
                incomingPosts.add(new Post(item));
            }
            for (var item : feed.f_user_items) {
                incomingUsers.add(new User(item));
            }

            postDao.upsertPosts(incomingPosts);
            userDao.upsertUsers(incomingUsers);
        }

        return result;
    }
}
