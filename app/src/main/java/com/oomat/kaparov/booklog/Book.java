package com.oomat.kaparov.booklog;

public class Book {

    private int mId;
    private boolean isInGoogleBooks;
    private String mTitle;
    private String mAuthor;
    private String mCategory;
    private int mPages;
    private String mImageUrl;
    private int mCurrentPage;
    private String mStartDate;
    private String mFinishDate;

    public Book(boolean isInGoogleBooks, String title, String author, String category, int pages, String imageUrl) {
        this.isInGoogleBooks = isInGoogleBooks;
        mTitle = title;
        mAuthor = author;
        mCategory = category;
        mPages = pages;
        mImageUrl = imageUrl;
    }

    public Book(int id, boolean isInGoogleBooks, String title, String author, String category,
                int pages, String imageUrl, int currentPage, String startDate, String finishDate) {
        mId = id;
        this.isInGoogleBooks = isInGoogleBooks;
        mTitle = title;
        mAuthor = author;
        mCategory = category;
        mPages = pages;
        mImageUrl = imageUrl;
        mCurrentPage = currentPage;
        mStartDate = startDate;
        mFinishDate = finishDate;
    }

    public int getId() {
        return mId;
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

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public String getFinishDate() {
        return mFinishDate;
    }

}

