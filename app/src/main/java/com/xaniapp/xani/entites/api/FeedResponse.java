package com.xaniapp.xani.entites.api;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class FeedResponse {

    public Date f_datetime_generated;
    public int f_u_id;
    public ArrayList<Item> f_items;

    public static class Item{
        public int f_p_id;
        public String f_p_content;
        @Nullable
        public Date f_p_datetime_created;
        @Nullable
        public Date f_p_datetime_edited;
        public int f_p_id_quote_of;
        public int f_p_id_reply_to;
        public String f_u_username;
        public int f_pi_likes;
        public int f_pi_repost;
        public int f_pi_quote;
    }
}
