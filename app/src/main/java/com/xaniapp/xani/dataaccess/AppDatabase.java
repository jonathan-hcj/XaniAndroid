package com.xaniapp.xani.dataaccess;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.xaniapp.xani.entites.da.Post;
import com.xaniapp.xani.entites.da.User;

@Database(entities = {User.class, Post.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase appDatabase;

    public abstract UserDAO userDao();
    public abstract PostDAO postDao();

    public static AppDatabase getDatabase(final Context context) {
        if (appDatabase == null) {
            synchronized (AppDatabase.class) {
                if (appDatabase == null) {
                    appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "xani.db")
                            .build();
                }
            }
        }
        return appDatabase;
    }

}