package com.example.kaparov.booklog;

public class Book {

    private boolean isInGoogleBooks;
    private String mTitle;
    private String mAuthor;
    private String mCategory;
    private int mPages;
    private String mImageUrl;

    public Book(boolean isInGoogleBooks, String title, String author, String category, int pages, String imageUrl) {
        this.isInGoogleBooks = isInGoogleBooks;
        mTitle = title;
        mAuthor = author;
        mCategory = category;
        mPages = pages;
        mImageUrl = imageUrl;
    }

    public boolean isInGoogleBooks() {
        return isInGoogleBooks;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getCategory() {
        return mCategory;
    }

    public int getPages() {
        return mPages;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}

