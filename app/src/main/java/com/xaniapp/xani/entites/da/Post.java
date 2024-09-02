package com.xaniapp.xani.entites.da;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "post")
public class Post {
    @PrimaryKey(autoGenerate = true)
    public long p_id;
    @ColumnInfo(name = "p_u_id")
    public int p_u_id;
    @ColumnInfo(name = "p_content")
    public String p_content;
    //  @ColumnInfo(name = "p_datetime_created")
    //  public Date p_datetime_created;
    //  @ColumnInfo(name = "p_datetime_edited")
    //  public Date p_datetime_edited;
    @ColumnInfo(name = "p_ps_id")
    public int p_ps_id;
    @ColumnInfo(name = "p_id_quote_of")
    public int p_id_quote_of;
    @ColumnInfo(name = "p_id_reply_to")
    public int p_id_reply_to;
}
