package com.example.maniekcs1995.defotapp;

public class Comment {

    private int id, user_id, defot_id;
    private String content, date;



    public Comment(int id, int defot_id, int user_id, String content, String date) {
        this.id = id;
        this.defot_id = defot_id;
        this.user_id = user_id;
        this.content = content;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getDefotId() {
        return defot_id;
    }

    public int getUser_id(){
        return user_id;
    }

    public String getContent() {return content; }

    public String getDate() {
        return date;
    }


}
