package com.xaniapp.xani.dataaccess;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Upsert;

import com.xaniapp.xani.entites.da.User;

import java.util.List;

@Dao
public interface UserDAO {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE u_id = :u_id")
    User getUser(int u_id);

    @Insert
    void insertAll(User ...users);

    @Upsert
    long upsertData(User user);
}
