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

    public void setInGoogleBooks(boolean inGoogleBooks) {
        isInGoogleBooks = inGoogleBooks;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public int getPages() {
        return mPages;
    }

    public void setPages(int pages) {
        mPages = pages;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}

