package com.example.maniekcs1995.defotapp;

public class Rating {
    private int defotId, rating;

    public Rating(int defotId, int rating){
        this.defotId = defotId;
        this.rating = rating;
    }

    public int getDefotId(){
        return defotId;
    }

    public int getRating(){
        return rating;
    }

    public int updateRating(int i){
        this.rating = rating + i;
        return rating;
    }
}
