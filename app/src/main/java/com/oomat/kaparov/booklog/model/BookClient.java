package com.oomat.kaparov.booklog.model;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class BookClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofit(){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl("https://www.googleapis.com/books/v1/")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }
}
