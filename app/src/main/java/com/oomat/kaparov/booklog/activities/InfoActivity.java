package com.oomat.kaparov.booklog.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.oomat.kaparov.booklog.R;
import com.oomat.kaparov.booklog.data.BookContract.BookEntry;
import com.oomat.kaparov.booklog.utils.UtilsBitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.oomat.kaparov.booklog.utils.Constants.EXISTING_BOOK_LOADER_ID;

public class InfoActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Content URI for the existing book
    private Uri mCurrentBookUri;

    private TextView mTitleTextView;
    private TextView mAuthorTextView;
    private TextView mCategoryTextView;
    private ImageView mBookImage;
    private RatingBar mBookRating;
    private TextView mCurrentPageTextView;
    private TextView mPagesTextView;
    private TextView mTextStartDate;
    private TextView mTextFinishDate;

    private Calendar mCalendar = Calendar.getInstance();

    //Pick start date
    private final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            view.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);

            String myFormat = "dd MMM yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            mTextStartDate.setText(sdf.format(mCalendar.getTime()));
        }
    };
    //Pick finish date
    private final DatePickerDialog.OnDateSetListener finishDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            view.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);

            String myFormat = "dd MMM yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            mTextFinishDate.setText(sdf.format(mCalendar.getTime()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //Set title of activity
        setTitle(getString(R.string.book_info));

        // Examine the intent that was used to launch this activity,
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Initialize a loader to read the book data from the database
        // and display the current values in the Book info
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER_ID, null, this);

        // Find all relevant views that we will need to read user input from
        mTitleTextView = findViewById(R.id.info_book_title);
        mAuthorTextView = findViewById(R.id.info_book_author);
        mCategoryTextView = findViewById(R.id.info_book_category);
        mBookImage = findViewById(R.id.info_book_image);
        mBookRating = findViewById(R.id.info_book_rating);
        mCurrentPageTextView = findViewById(R.id.from_edit_current_page);
        ImageView editCurrentPage = findViewById(R.id.edit_current_page);
        mPagesTextView = findViewById(R.id.text_total_page_from_database);
        ImageView editStartDate = findViewById(R.id.edit_start_date);
        mTextStartDate = findViewById(R.id.from_edit_start_date);
        ImageView editFinishDate = findViewById(R.id.edit_finish_date);
        mTextFinishDate = findViewById(R.id.from_edit_finish_date);

        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(InfoActivity.this, startDate, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editFinishDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(InfoActivity.this, finishDate, mCalendar
                        .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        editCurrentPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickNumber();
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that contains all columns from the books table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_TITLE,
                BookEntry.COLUMN_AUTHOR,
                BookEntry.COLUMN_CATEGORY,
                BookEntry.COLUMN_PAGES,
                BookEntry.COLUMN_IMAGE,
                BookEntry.COLUMN_RATING,
                BookEntry.COLUMN_CURRENT_PAGE,
                BookEntry.COLUMN_START_DATE,
                BookEntry.COLUMN_FINISH_DATE};

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
            int currentPageColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_CURRENT_PAGE);
            int startDateColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_START_DATE);
            int finishDateColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_FINISH_DATE);

            // Extract out the value from the Cursor for the given column index
            String bookTitle = cursor.getString(titleColumnIndex);
            String bookAuthor = cursor.getString(authorColumnIndex);
            String bookCategory = cursor.getString(categoryColumnIndex);
            int bookPages = cursor.getInt(pagesColumnIndex);
            Float bookRating = cursor.getFloat(ratingColumnIndex);
            byte[] bookImage = cursor.getBlob(imageColumnIndex);
            int bookCurrentPage = cursor.getInt(currentPageColumnIndex);
            String bookStartDate = cursor.getString(startDateColumnIndex);
            String bookFinishDate = cursor.getString(finishDateColumnIndex);

            // Update the views on the screen with the values from the database
            Bitmap bitmap = UtilsBitmap.getImage(bookImage);
            mBookImage.setImageBitmap(bitmap);
            mTitleTextView.setText(bookTitle);
            mAuthorTextView.setText(bookAuthor);
            mCategoryTextView.setText(bookCategory);
            mPagesTextView.setText(String.valueOf(bookPages));
            mBookRating.setRating(bookRating);
            mCurrentPageTextView.setText(String.valueOf(bookCurrentPage));
            mTextStartDate.setText(bookStartDate);
            mTextFinishDate.setText(bookFinishDate);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleTextView.setText("");
        mAuthorTextView.setText("");
        mCategoryTextView.setText("");
        mBookRating.setRating(0);
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
                Intent intent = new Intent(InfoActivity.this, EditorActivity.class);

                // Set the URI on the data field of the intent
                intent.setData(mCurrentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current book.
                startActivity(intent);

                //save changes
                saveChanges();

                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:

                //save changes
                saveChanges();

                NavUtils.navigateUpFromSameTask(InfoActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //save changes
        saveChanges();
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
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

    //Pick current page number
    private void pickNumber() {
        new MaterialDialog.Builder(this)
                .title(R.string.set_current_page)
                .titleColorRes(R.color.colorBlackText)
                .backgroundColorRes(R.color.colorPrimaryText)
                .contentColorRes(R.color.colorBlackText)
                .positiveColorRes(R.color.colorAccent)
                .widgetColorRes(R.color.colorAccent)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(R.string.enter_number, R.string.input_prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().equals(""))
                            mCurrentPageTextView.setText("0");
                        else if (Integer.valueOf(input.toString()) <= Integer.valueOf(mPagesTextView.getText().toString())
                                && Integer.valueOf(input.toString()) >= 0)
                            mCurrentPageTextView.setText(input.toString());
                        else
                            Toast.makeText(InfoActivity.this, "Set page less than " + mPagesTextView.getText().toString(),
                                    Toast.LENGTH_LONG).show();
                    }
                }).show();
    }

    private void saveChanges() {
        // Read from input fields
        Integer currentPageInteger = Integer.valueOf(mCurrentPageTextView.getText().toString().trim());
        String startDateString = mTextStartDate.getText().toString().trim();
        String finishDateString = mTextFinishDate.getText().toString().trim();

        //save to database
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_CURRENT_PAGE, currentPageInteger);
        values.put(BookEntry.COLUMN_START_DATE, startDateString);
        values.put(BookEntry.COLUMN_FINISH_DATE, finishDateString);
        getContentResolver().update(mCurrentBookUri, values, null, null);
    }

    protected void onStop() {
        super.onStop();
        Drawable d = mBookImage.getDrawable();
        if (d != null) d.setCallback(null);
        mBookImage.setImageDrawable(null);
    }



}
