package com.example.kaparov.booklog;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.kaparov.booklog.data.BookContract.*;
import com.example.kaparov.booklog.utils.UtilsBitmap;


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

        // Find the columns of book attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_TITLE);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR);
        int categoryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_CATEGORY);
        int pagesColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PAGES);
        int imageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_IMAGE);
        int ratingColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_RATING);
//        int isbnColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_ISBN);


        // Read the book attributes from the Cursor for the current book
        String bookTitle = cursor.getString(titleColumnIndex);
        String bookAuthor = cursor.getString(authorColumnIndex);
        String bookCategory = cursor.getString(categoryColumnIndex);
        String bookPages = cursor.getString(pagesColumnIndex);
        float bookRating = cursor.getFloat(ratingColumnIndex);
        byte[] bookImage = cursor.getBlob(imageColumnIndex);
//        String bookIsbn = cursor.getString(isbnColumnIndex);


//        if (TextUtils.isEmpty(bookAuthor)) {
//            bookAuthor = context.getString(R.string.unknown_author);
//        }
//
//        if (TextUtils.isEmpty(bookCategory)) {
//            bookCategory = context.getString(R.string.unknown_category);
//        }

        // Update the TextViews with the attributes for the current book
        titleTextView.setText(bookTitle);
        authorTextView.setText(bookAuthor);
        categoryTextView.setText(bookCategory);
        ratingBar.setRating(bookRating);

        Bitmap bitmap = UtilsBitmap.getImage(bookImage);
//                bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);  // TODO: 4/15/17
        imageView.setImageBitmap(bitmap);

//        //photo.setImageURI(Uri.parse("Location");
//        BitmapDrawable drawable = (BitmapDrawable) photo.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//        bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
//        photo.setImageBitmap(bitmap);

    }
}
