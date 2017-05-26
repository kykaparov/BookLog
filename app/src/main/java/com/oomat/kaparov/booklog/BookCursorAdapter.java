package com.oomat.kaparov.booklog;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.oomat.kaparov.booklog.data.BookContract.*;
import com.oomat.kaparov.booklog.utils.UtilsBitmap;


/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView titleTextView = (TextView) view.findViewById(R.id.book_title);
        TextView authorTextView = (TextView) view.findViewById(R.id.book_author);
        TextView categoryTextView = (TextView) view.findViewById(R.id.book_category);
        ImageView imageView = (ImageView) view.findViewById(R.id.book_image);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.book_rating);

        TextView startTextView = (TextView) view.findViewById(R.id.catalog_start_date);
        TextView finishTextView = (TextView) view.findViewById(R.id.catalog_finish_date);
        TextView percentTextView = (TextView) view.findViewById(R.id.catalog_percent);
        TextView ratioTextView = (TextView) view.findViewById(R.id.catalog_pages_ratio);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.catalog_progress_bar);

        // Find the columns of book attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_TITLE);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR);
        int categoryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_CATEGORY);
        int pagesColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PAGES);
        int imageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_IMAGE);
        int ratingColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_RATING);
        int currentColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_CURRENT_PAGE);
        int startColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_START_DATE);
        int finishColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_FINISH_DATE);


        // Read the book attributes from the Cursor for the current book
        String bookTitle = cursor.getString(titleColumnIndex);
        String bookAuthor = cursor.getString(authorColumnIndex);
        String bookCategory = cursor.getString(categoryColumnIndex);
        int bookPages = cursor.getInt(pagesColumnIndex);
        byte[] bookImage = cursor.getBlob(imageColumnIndex);
        float bookRating = cursor.getFloat(ratingColumnIndex);
        int bookCurrent = cursor.getInt(currentColumnIndex);
        String bookStart = cursor.getString(startColumnIndex);
        String bookFinish = cursor.getString(finishColumnIndex);

        // Update the TextViews with the attributes for the current book
        titleTextView.setText(bookTitle);
        authorTextView.setText(bookAuthor);
        categoryTextView.setText(bookCategory);
        ratingBar.setRating(bookRating);

        Bitmap bitmap = UtilsBitmap.getImage(bookImage);
        imageView.setImageBitmap(bitmap);

        startTextView.setText(bookStart);
        finishTextView.setText(bookFinish);
        int ratio = 100 * bookCurrent / bookPages;
        percentTextView.setText(ratio + "%");
        ratioTextView.setText(bookCurrent + "/" + bookPages);
        progressBar.setProgress(ratio);


        //TODO: 5/19/17
//        //photo.setImageURI(Uri.parse("Location");
//        BitmapDrawable drawable = (BitmapDrawable) photo.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//        bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
//        photo.setImageBitmap(bitmap);
    }

}
