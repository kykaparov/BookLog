package com.example.kaparov.booklog;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kaparov.booklog.data.BookContract.*;
import com.github.clans.fab.FloatingActionMenu;


/**
 * Displays list of books that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the pet data loader */
    private static final int PET_LOADER = 0;

    /** Adapter for the ListView */
    BookCursorAdapter  mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        final FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.fabmenu);
        fabMenu.setClosedOnTouchOutside(true);


        final FloatingActionButton fabBtnScanner = (FloatingActionButton) findViewById(R.id.fab_scanner);
        fabBtnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(CatalogActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });


        FloatingActionButton fabBtnAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the book data
        ListView bookListView = (ListView) findViewById(R.id.listview);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link BookEntry#CONTENT_URI}.
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current book.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

//    /**
//     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
//     */
//    private void insertPet() {
//        // Create a ContentValues object where column names are the keys,
//        // and Toto's pet attributes are the values.
//        ContentValues values = new ContentValues();
//        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
//        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
//        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
//        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);
//
//        // Insert a new row for Toto into the provider using the ContentResolver.
//        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
//        // into the pets database table.
//        // Receive the new content URI that will allow us to access Toto's data in the future.
//        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
//    }
//
//    /**
//     * Helper method to delete all pets in the database.
//     */
//    private void deleteAllPets() {
//        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
//        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        displayDatabaseInfo();
//    }
//
//    private void displayDatabaseInfo() {
//
//        String[] projection = {
//                BookEntry._ID,
//                BookEntry.COLUMN_TITLE,
//                BookEntry.COLUMN_AUTHOR,
//                BookEntry.COLUMN_CATEGORY,
//                BookEntry.COLUMN_PAGES,
//                BookEntry.COLUMN_RATING};
//
//        Cursor cursor = getContentResolver().query(BookEntry.CONTENT_URI, projection, null, null, null);
//
//        ListView petListView = (ListView) findViewById(R.id.listview);
//
//        BookCursorAdapter adapter = new BookCursorAdapter(this, cursor);
//
//        View emptyView = findViewById(R.id.empty_view);
//        petListView.setEmptyView(emptyView);
//
//        petListView.setAdapter(adapter);
//
//
//    }
//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_TITLE,
                BookEntry.COLUMN_AUTHOR,
                BookEntry.COLUMN_CATEGORY,
                BookEntry.COLUMN_RATING };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,  // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link BookCursorAdapter} with this new cursor containing updated book data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
