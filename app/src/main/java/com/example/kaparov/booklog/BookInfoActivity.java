package com.example.kaparov.booklog;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaparov.booklog.data.BookContract;

public class BookInfoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 1;

    //Content URI for the existing book
    private Uri mCurrentBookUri;

    private TextView mTitleTextView;
    private TextView mAuthorTextView;
    private TextView mCategoryTextView;
    private TextView mPagesTextView;
    private ImageView mImageView;
    private RatingBar mBookRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        //Set title of activity
        setTitle(getString(R.string.book_info));

        // Examine the intent that was used to launch this activity,
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Initialize a loader to read the book data from the database
        // and display the current values in the Book info
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

        // Find all relevant views that we will need to read user input from
        mTitleTextView = (TextView) findViewById(R.id.info_book_title);
        mAuthorTextView = (TextView) findViewById(R.id.info_book_author);
        mCategoryTextView = (TextView) findViewById(R.id.info_book_category);
//        mPagesTextView = (TextView) findViewById(R.id.info_book_pages);
        mImageView = (ImageView) findViewById(R.id.info_book_image);
        mBookRating = (RatingBar) findViewById(R.id.info_book_rating);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that contains all columns from the books table
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_TITLE,
                BookContract.BookEntry.COLUMN_AUTHOR,
                BookContract.BookEntry.COLUMN_CATEGORY,
                BookContract.BookEntry.COLUMN_PAGES,
                BookContract.BookEntry.COLUMN_IMAGE,
                BookContract.BookEntry.COLUMN_RATING};
//                BookEntry.COLUMN_ISBN };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,        // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_AUTHOR);
            int categoryColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_CATEGORY);
            int pagesColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PAGES);
            int imageColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_IMAGE);
            int ratingColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_RATING);
//            int isbnColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_ISBN);

            // Extract out the value from the Cursor for the given column index
            String bookTitle = cursor.getString(titleColumnIndex);
            String bookAuthor = cursor.getString(authorColumnIndex);
            String bookCategory = cursor.getString(categoryColumnIndex);
            String bookPages = cursor.getString(pagesColumnIndex);
            Float bookRating = cursor.getFloat(ratingColumnIndex);
            byte[] bookImage = cursor.getBlob(imageColumnIndex);
//            String bookIsbn = cursor.getString(isbnColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleTextView.setText(bookTitle);
            mAuthorTextView.setText(bookAuthor);
            mCategoryTextView.setText(bookCategory);
//            mPagesTextView.setText(bookPages);
            mBookRating.setRating(bookRating);

            Bitmap bitmap = UtilsBitmap.getImage(bookImage);
            mImageView.setImageBitmap(bitmap);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleTextView.setText("");
        mAuthorTextView.setText("");
        mCategoryTextView.setText("");
//        mPagesTextView.setText("");
        mBookRating.setRating(0);

//        Bitmap bitmap = UtilsBitmap.getImage(null);
//        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_edit_book:
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(BookInfoActivity.this, EditorActivity.class);

                // Set the URI on the data field of the intent
                intent.setData(mCurrentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current book.
                startActivity(intent);

                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(BookInfoActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }


}
