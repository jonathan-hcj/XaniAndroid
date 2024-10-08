package com.xaniapp.xani.dataaccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import com.xaniapp.xani.entites.da.Post;

import java.util.List;

@Dao
public interface PostDAO {
    @Query("SELECT * FROM post")
    List<Post> getAll();
    @Insert
    void insertAll(Post ...posts);
    @Upsert
    void upsertPosts(List<Post> posts);
    @Upsert
    long upsertPost(Post post);
}
