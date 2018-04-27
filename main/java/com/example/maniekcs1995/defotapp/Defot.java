package com.example.maniekcs1995.defotapp;

import android.graphics.Bitmap;

public class Defot {
        private int id, user_id;
        private String title;
        private String desc;
        private String url, date;
        private Bitmap image;


        public Defot(int id, String title, String desc, String url, Bitmap image, String date, int user_id) {
            this.id = id;
            this.title = title;
            this.desc = desc;
            this.url = url;
            this.image = image;
            this.date = date;
            this.user_id = user_id;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDesc() {
            return desc;
        }

        public String getURL() {
            return url;
        }

        public Bitmap getImage() {
            return image;
        }

        public String getDate() {
            return date;
        }

        public int getUser_id(){
            return user_id;
        }
}

