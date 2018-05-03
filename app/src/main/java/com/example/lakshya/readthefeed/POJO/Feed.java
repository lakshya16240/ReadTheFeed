package com.example.lakshya.readthefeed.POJO;

import java.net.URL;

public class Feed {

    public String title;
    public String link;
    public String description;
    public String imageUrl;

    public Feed(String title, String link, String description, String imageUrl) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
