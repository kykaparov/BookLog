package com.oomat.kaparov.booklog.model;


import com.oomat.kaparov.booklog.model.google.GoogleBook;
import com.oomat.kaparov.booklog.model.google.VolumeInfo;

import static com.oomat.kaparov.booklog.utils.StringUtils.stringFromList;

public class Book {
    private String title;
    private String authors;
    private String categories;
    private int pageCount;
    private String imageLink;

    private boolean isGoogleBookAvailable;

    public Book(GoogleBook googleBook) {
        mapToGoogleBook(googleBook);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public boolean isGoogleBookAvailable() {
        return isGoogleBookAvailable;
    }

    private void mapToGoogleBook(GoogleBook googleBook) {
        if (googleBook != null) {
            isGoogleBookAvailable = true;
            VolumeInfo info = googleBook.getItems().get(0).getVolumeInfo();
            this.title = info.getTitle() + " " + info.getSubtitle();
            this.authors = stringFromList(info.getAuthors());
            this.categories = stringFromList(info.getCategories());
            this.pageCount = info.getPageCount();
            this.imageLink = info.getImageLinks().getBigImage();
        } else {
            isGoogleBookAvailable = false;
        }
    }
}
