package com.xaniapp.xani.entites.api;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class FeedResponse {

    public Date f_datetime_generated;
    public int f_u_id;
    public ArrayList<PostItem> f_post_items;
    public ArrayList<UserItem> f_user_items;

    public static class PostItem {

        public long p_id;
        public int p_u_id;
        @Nullable
        public String p_content;
        public short p_ps_id;
        @Nullable
        public Date p_datetime_created;
        @Nullable
        public Date p_datetime_edited;
        public int p_id_quote_of;
        public int p_id_reply_to;

        /* post stats */
        public int p_total_replies;
        public int p_total_likes;
        public int p_total_reposts;
        public int p_total_quotes;
    }

    public static class UserItem {
        public int u_id;
        @Nullable
        public String u_username;
        @Nullable
        public String u_avitar;
        @Nullable
        public String u_description;
        @Nullable
        public Date u_joined_date;
    }
}
