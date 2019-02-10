package com.oomat.kaparov.booklog.model.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageLinks {

    @JsonProperty("smallThumbnail")
    private String smallImage;

    @JsonProperty("thumbnail")
    private String bigImage;

    public String getSmallImage() {
        return smallImage;
    }

    public String getBigImage() {
        return bigImage;
    }
}
