package com.oomat.kaparov.booklog.model;

import com.oomat.kaparov.booklog.model.google.GoogleBook;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookService {
    @GET("volumes/")
    Call<GoogleBook> requestBook(@Query("q") String q);
}
