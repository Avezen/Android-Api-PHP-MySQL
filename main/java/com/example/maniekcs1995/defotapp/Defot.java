package com.example.maniekcs1995.defotapp;

public class Defot {
        private int id, user_id, rating;
        private String title;
        private String desc;
        private String url, date;



        public Defot(int id, String title, String desc, String url, int rating, String date, int user_id) {
            this.id = id;
            this.title = title;
            this.desc = desc;
            this.url = url;
            this.rating = rating;
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

        public int getRating() {
            return rating;
        }

        public void setRating(int rate) {
        this.rating = rate;
    }

        public String getDate() {
            return date;
        }

        public int getUser_id(){
            return user_id;
        }
}

