package com.xaniapp.xani.entites.da;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.xaniapp.xani.entites.api.FeedResponse;

import java.util.Date;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = false)
    public long u_id;
    @ColumnInfo(name = "u_username")
    public String u_username;
    @ColumnInfo(name = "u_avitar")
    public String u_avitar;
    @ColumnInfo(name = "u_description")
    public String u_description;
    public @ColumnInfo(name = "u_joined_date")
    @Nullable Date u_joined_date;

    public User() {}
    public User(FeedResponse.UserItem apiUser) {
        this.u_id = apiUser.u_id;
        this.u_username = apiUser.u_username;
        this.u_avitar = apiUser.u_avitar;
        this.u_description = apiUser.u_description;
        this.u_joined_date = apiUser.u_joined_date;
    }
}

