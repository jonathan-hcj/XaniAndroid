package com.xaniapp.xani.dataaccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.xaniapp.xani.entites.da.Post;

import java.util.List;

@Dao
public interface PostDAO {
    @Query("SELECT * FROM post")
    List<Post> getAll();

    @Insert
    void insertAll(Post ...posts);
}
