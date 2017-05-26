package com.oomat.kaparov.booklog.utils;

import android.text.TextUtils;
import android.util.Log;

import com.oomat.kaparov.booklog.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Helper methods related to requesting and receiving data from Google Books API.
 */
public final class UtilsQuery {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = UtilsQuery.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link UtilsQuery} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name UtilsQuery (and an object instance of UtilsQuery is not needed).
     */
    private UtilsQuery() {
    }

    /**
     * Query url and return a {@link Book} object.
     */
    public static Book fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a {@link Book} object that has been built up from
     * parsing the given JSON response.
     */
    private static Book extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        Book book = null;
        String title;
        String authors;
        String category;
        int pages;
        String imageUrl;


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            if (baseJsonResponse.has("items")) {

                //if isInGoogleBooks = true;

                JSONArray bookArray = baseJsonResponse.getJSONArray("items");

                JSONObject currentBook = bookArray.getJSONObject(0);

                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                if (volumeInfo.has("subtitle"))
                    title = volumeInfo.getString("title") + " " + volumeInfo.getString("subtitle");
                else
                    title = volumeInfo.getString("title");

                if (volumeInfo.has("authors")) {
                    JSONArray authorArray = volumeInfo.getJSONArray("authors");
                    authors = authorArray.getString(0);
                    for (int i = 1; i < authorArray.length(); i++) {
                        authors += " " + authorArray.getString(i);
                    }
                } else authors = "";

                if (volumeInfo.has("categories")) {
                    JSONArray categoryArray = volumeInfo.getJSONArray("categories");
                    category = categoryArray.getString(0);
                    for (int i = 1; i < categoryArray.length(); i++) {
                        category += " " + categoryArray.getString(i);
                    }
                } else category = "";

                if (volumeInfo.has("pageCount"))
                    pages = volumeInfo.getInt("pageCount");
                else
                    pages = 0;

                if (volumeInfo.has("pageCount")) {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    imageUrl = imageLinks.getString("smallThumbnail");
                    if (imageLinks.getString("smallThumbnail") == null)
                        imageUrl = imageLinks.getString("thumbnail");
                } else imageUrl = "";

                // Create a new {@link Book} object from the JSON response.
                book = new Book(true, title, authors, category, pages, imageUrl);

            } else {
                //if isInGoogleBooks = false;
                book = new Book(false, null, null, null, 1, null);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("UtilsQuery", "Problem parsing the earthquake JSON results", e);
        }

        return book;
    }

}
