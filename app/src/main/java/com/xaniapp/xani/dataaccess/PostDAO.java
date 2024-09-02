package com.xaniapp.xani.dataaccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import com.xaniapp.xani.entites.da.Post;
import com.xaniapp.xani.entites.da.User;

import java.util.List;

@Dao
public interface PostDAO {
    @Query("SELECT * FROM post")
    List<Post> getAll();

    @Insert
    void insertAll(Post ...posts);

    @Upsert
    long upsertData(Post post);
}
