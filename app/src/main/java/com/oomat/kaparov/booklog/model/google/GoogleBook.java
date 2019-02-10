package com.oomat.kaparov.booklog.model.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBook {

    @JsonProperty("items")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }
}