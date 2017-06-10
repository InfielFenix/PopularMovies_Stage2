package com.projects.alexanderauer.popularmovies.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 25.04.2017.
 *
 * Entity class for a Trailer object
 *
 */

public class Trailer {

    @SerializedName("name")
    String name;
    @SerializedName("key")
    String key;

    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
