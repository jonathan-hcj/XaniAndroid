package com.xaniapp.xani.entites.da;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.xaniapp.xani.entites.api.FeedResponse;

import java.util.Date;

@Entity(tableName = "post")
public class Post {
    @PrimaryKey(autoGenerate = true)
    public long p_id;
    @ColumnInfo(name = "p_u_id")
    public int p_u_id;
    @ColumnInfo(name = "p_content")
    public String p_content;
    @ColumnInfo(name = "p_datetime_created")
    public @Nullable Date p_datetime_created;
    @ColumnInfo(name = "p_datetime_edited")
    public Date p_datetime_edited;
    @ColumnInfo(name = "p_ps_id")
    public int p_ps_id;
    @ColumnInfo(name = "p_id_quote_of")
    public int p_id_quote_of;
    @ColumnInfo(name = "p_id_reply_to")
    public int p_id_reply_to;

    @ColumnInfo(name = "p_total_replies")
    public int p_total_replies;
    @ColumnInfo(name = "p_total_likes")
    public int p_total_likes;
    @ColumnInfo(name = "p_total_reposts")
    public int p_total_reposts;
    @ColumnInfo(name = "p_total_quotes")
    public int p_total_quotes;

    public Post() {}
    public Post(FeedResponse.PostItem apiPost) {

        this.p_id = apiPost.p_id;
        this.p_u_id = apiPost.p_u_id;
        this.p_content = apiPost.p_content;
        this.p_datetime_created = apiPost.p_datetime_created;
        this.p_datetime_edited = apiPost.p_datetime_edited;
        this.p_ps_id = apiPost.p_ps_id;
        this.p_id_quote_of = apiPost.p_id_quote_of;
        this.p_id_reply_to = apiPost.p_id_reply_to;
        this.p_total_replies = apiPost.p_total_replies;
        this.p_total_likes = apiPost.p_total_likes;
        this.p_total_reposts = apiPost.p_total_reposts;
        this.p_total_quotes = apiPost.p_total_quotes;
    }
}
