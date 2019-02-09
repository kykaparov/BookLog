package com.oomat.kaparov.booklog.activities;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.oomat.kaparov.booklog.BookCursorAdapter;
import com.oomat.kaparov.booklog.R;
import com.oomat.kaparov.booklog.data.BookContract.*;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import static com.oomat.kaparov.booklog.utils.Constants.*;

/**
 * Displays list of books that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener {

    private int mStatus;

    /** Adapter for the ListView */
    private BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionMenu fabMenu = findViewById(R.id.fabmenu);
        fabMenu.setClosedOnTouchOutside(true);

        final FloatingActionButton fabBtnScanner = findViewById(R.id.fab_scanner);
        fabBtnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(CatalogActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });

        final FloatingActionButton fabBtnAdd = findViewById(R.id.fab_add);
        fabBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Find the ListView which will be populated with the book data
        ListView bookListView = findViewById(R.id.listView);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, InfoActivity.class);

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
        getLoaderManager().initLoader(mStatus, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
        if (id == R.id.action_scan_book) {
            Intent intent = new Intent(CatalogActivity.this, ScannerActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_manually_add) {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int status, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
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

        String selection;
        String[] selectionArgs = {"0"};

        //Select reading status and define query selection
        switch (status) {
            case BOOK_TO_READ:
                selection = "CAST(" + BookEntry.COLUMN_CURRENT_PAGE + " AS TEXT)=?";
                break;
            case BOOK_READING_NOW:
                selection = "CAST(" + BookEntry.COLUMN_CURRENT_PAGE + " AS TEXT)!=?"
                        + " AND " + BookEntry.COLUMN_CURRENT_PAGE + "<" + BookEntry.COLUMN_PAGES;
                break;
            case BOOK_HAVE_READ:
                selection = "CAST(" + BookEntry.COLUMN_CURRENT_PAGE + " AS TEXT)!=?"
                        + " AND " + BookEntry.COLUMN_CURRENT_PAGE + "=" + BookEntry.COLUMN_PAGES;
                break;
            default:
                selection = null;
                selectionArgs = null;
        }

        return new CursorLoader(this, BookEntry.CONTENT_URI, projection,
                selection, selectionArgs, null);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_all:
                mStatus = BOOK_ALL;
                break;
            case R.id.nav_to_read:
                mStatus = BOOK_TO_READ;
                break;
            case R.id.nav_reading:
                mStatus = BOOK_READING_NOW;
                break;
            case R.id.nav_have_read:
                mStatus = BOOK_HAVE_READ;
                break;
        }

        //Restart Loader with a new reading status value
        getLoaderManager().initLoader(mStatus, null, this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
