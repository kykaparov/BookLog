package com.example.kaparov.booklog;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kaparov.booklog.data.BookContract.*;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the book data loader */
    private static final int EXISTING_BOOK_LOADER = 0;

    private static final int SELECT_PICTURE = 1;

    /** Content URI for the existing book (null if it's a new book) */
    private Uri mCurrentBookUri;

    private EditText mTitleEditText;
    private EditText mAuthorEditText;
    private EditText mCategoryEditText;
    private EditText mPagesEditText;
    private ImageView mImageView;
    private RatingBar mBookRating;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If the intent DOES NOT contain a book content URI, then we know that we are
        // creating a new book.
        if (mCurrentBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle(getString(R.string.add_new_book));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing book, so change app bar to say "Edit Book"
            setTitle(getString(R.string.edit_book));

            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mTitleEditText = (EditText) findViewById(R.id.edit_book_title);
        mAuthorEditText = (EditText) findViewById(R.id.edit_book_author);
        mCategoryEditText = (EditText) findViewById(R.id.edit_book_category);
        mPagesEditText = (EditText) findViewById(R.id.edit_book_pages);
        mImageView = (ImageView) findViewById(R.id.edit_book_image);
        mBookRating = (RatingBar) findViewById(R.id.edit_book_rating);

        //Extract values from Scanner Avtivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String titleExtra = extras.getString("title");
            String authorExtra = extras.getString("author");
            String categoryExtra = extras.getString("category");
            String pagesExtra = extras.getString("pages");
            String imageExtra = extras.getString("image");

            mTitleEditText.setText(titleExtra);
            mAuthorEditText.setText(authorExtra);
            mCategoryEditText.setText(categoryExtra);
            mPagesEditText.setText(pagesExtra);

            if (extras.getBoolean("isInGoogleBooks")) {
                Glide.with(EditorActivity.this).
                        load(imageExtra).
                        asBitmap().
                        into(mImageView);
            }
        }

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mTitleEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mCategoryEditText.setOnTouchListener(mTouchListener);
        mPagesEditText.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);
        mBookRating.setOnTouchListener(mTouchListener);

//        mImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openImageChooser();
//            }
//        });


    }

    /**
     * Get user input from editor and save book into database.
     */
    private int saveBook() {
        // Read from input fields
        String titleString = mTitleEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String categoryString = mCategoryEditText.getText().toString().trim();
        String pagesString = mPagesEditText.getText().toString().trim();
        Float rating = mBookRating.getRating();

        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        byte[] imageByte = UtilsBitmap.getBytes(bitmap);

        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(authorString) &&
                TextUtils.isEmpty(categoryString) && TextUtils.isEmpty(pagesString)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return 0;
        }

        // Check that the title is not null
        if (TextUtils.isEmpty(titleString)) {
            return 1;
        }

        // Check that the pages are not null
        if (TextUtils.isEmpty(pagesString)) {
            return 1;
        }

        // No need to check the author, category, rating, any value is valid (including null).

        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_TITLE, titleString);
        values.put(BookEntry.COLUMN_AUTHOR, authorString);
        values.put(BookEntry.COLUMN_CATEGORY, categoryString);
        values.put(BookEntry.COLUMN_PAGES, pagesString);
        values.put(BookEntry.COLUMN_IMAGE, imageByte);
        values.put(BookEntry.COLUMN_RATING, rating);

        // Determine if this is a new or existing book by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: mCurrentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentbookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return 0;
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                // Save book to database
                if (saveBook() == 0) {
                    // Exit activity
                    finish();
                    return true;
                } else {
                    Toast.makeText(this, getString(R.string.toast_require), Toast.LENGTH_SHORT).show();
                    return false;
                }
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the books table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_TITLE,
                BookEntry.COLUMN_AUTHOR,
                BookEntry.COLUMN_CATEGORY,
                BookEntry.COLUMN_PAGES,
                BookEntry.COLUMN_IMAGE,
                BookEntry.COLUMN_RATING};
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
            int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_AUTHOR);
            int categoryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_CATEGORY);
            int pagesColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PAGES);
            int imageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_IMAGE);
            int ratingColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_RATING);
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
            mTitleEditText.setText(bookTitle);
            mAuthorEditText.setText(bookAuthor);
            mCategoryEditText.setText(bookCategory);
            mPagesEditText.setText(bookPages);
            mBookRating.setRating(bookRating);

            Bitmap bitmap = UtilsBitmap.getImage(bookImage);
            mImageView.setImageBitmap(bitmap);

        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleEditText.setText("");
        mAuthorEditText.setText("");
        mCategoryEditText.setText("");
        mPagesEditText.setText("");
        mBookRating.setRating(0);

//        Bitmap bitmap = UtilsBitmap.getImage(null);
//        mImageView.setImageBitmap(bitmap);

    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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


    // Choose an image from Gallery
//    void openImageChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_VIEW);
//        startActivityForResult(Intent.createChooser(intent, "Select a book image"), SELECT_PICTURE);
//    }




////    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        if (resultCode == RESULT_OK) {
////            if (requestCode == SELECT_PICTURE) {
////
////                Uri selectedImageUri = data.getData();
////
////                mImageView.setImageURI(selectedImageUri);
//
//
////                if (null != selectedImageUri) {
////
////                    // Saving to Database...
////                    if (saveImageInDB(selectedImageUri)) {
////                        showMessage("Image Saved in Database...");
////                        imgView.setImageURI(selectedImageUri);
////                    }
////
////                    // Reading from Database after 3 seconds just to show the message
////                    new Handler().postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////                            if (loadImageFromDB()) {
////                                showMessage("Image Loaded from Database...");
////                            }
////                        }
////                    }, 3000);
////                }
//
//            }
//        }
//    }

//    // Save the
//    Boolean saveImageInDB(Uri selectedImageUri) {
//
//        try {
//            dbHelper.open();
//            InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
//            byte[] inputData = Utils.getBytes(iStream);
//            dbHelper.insertImage(inputData);
//            dbHelper.close();
//            return true;
//        } catch (IOException ioe) {
//            Log.e(TAG, "<saveImageInDB> Error : " + ioe.getLocalizedMessage());
//            dbHelper.close();
//            return false;
//        }
//
//    }
//
//    Boolean loadImageFromDB() {
//        try {
//            dbHelper.open();
//            byte[] bytes = dbHelper.retreiveImageFromDB();
//            dbHelper.close();
//            // Show Image from DB in ImageView
//            imgView.setImageBitmap(Utils.getImage(bytes));
//            return true;
//        } catch (Exception e) {
//            Log.e(TAG, "<loadImageFromDB> Error : " + e.getLocalizedMessage());
//            dbHelper.close();
//            return false;
//        }
//    }
}
