package com.xaniapp.xani.entites.da;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
    public class User {

        @PrimaryKey(autoGenerate = false)
        public long u_id;
        @ColumnInfo(name = "u_username")
        public String u_username;
        @ColumnInfo(name = "u_email_address")
        public String u_email_address;
        @ColumnInfo(name = "u_password_hash")
        public String u_password_hash;
}

