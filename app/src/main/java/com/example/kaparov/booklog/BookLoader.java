package com.example.kaparov.booklog;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.kaparov.booklog.utils.UtilsQuery;

/**
 * Loads a book by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class BookLoader extends AsyncTaskLoader<Book> {

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public Book loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a book.
        Book book = UtilsQuery.fetchBookData(mUrl);
        return book;
    }
}
