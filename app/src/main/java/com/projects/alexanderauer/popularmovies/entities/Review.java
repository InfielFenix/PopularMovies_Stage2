package com.projects.alexanderauer.popularmovies.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 25.04.2017.
 *
 *  Entity class for a Review object
 *
 */

public class Review {

    @SerializedName("author")
    String author;
    @SerializedName("content")
    String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
