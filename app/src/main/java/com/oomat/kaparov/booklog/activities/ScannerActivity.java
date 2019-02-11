package com.oomat.kaparov.booklog.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.oomat.kaparov.booklog.model.Book;
import com.oomat.kaparov.booklog.BookLoader;
import com.oomat.kaparov.booklog.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.oomat.kaparov.booklog.utils.Constants.*;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler,
        LoaderManager.LoaderCallbacks<Book> {

    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private Toolbar mToolbar;
    private String bookISBN;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }

        if(savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(FLASH_STATE, false);
        } else {
            mFlash = false;
        }
        setContentView(R.layout.activity_scanner);

        mToolbar = findViewById(R.id.scannerToolbar);
        mToolbar.setTitle(R.string.scan_toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        ViewGroup contentFrame = findViewById(R.id.scannerFrame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.setAutoFocus(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem menuItem;

        if(mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_simple_add_flash, 0, R.string.menu_flash_on);
            menuItem.setIcon(R.drawable.ic_flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_simple_add_flash, 0, R.string.menu_flash_off);
            menuItem.setIcon(R.drawable.ic_flash_off);
        }

        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_simple_add_flash:
                mFlash = !mFlash;
                if(mFlash) {
                    item.setTitle(R.string.menu_flash_on);
                    item.setIcon(R.drawable.ic_flash_on);
                } else {
                    item.setTitle(R.string.menu_flash_off);
                    item.setIcon(R.drawable.ic_flash_off);}
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.action_search:
                searchIsbnDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.setAutoFocus(true);
        mScannerView.setFlash(mFlash);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScannerActivity.this);
            }
        }, 2000);
        handleSearch(rawResult.getText());
    }

    private void searchIsbnDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.search_isbn_manually)
                .titleColorRes(R.color.colorBlackText)
                .backgroundColorRes(R.color.colorPrimaryText)
                .contentColorRes(R.color.colorBlackText)
                .positiveColorRes(R.color.colorAccent)
                .widgetColorRes(R.color.colorAccent)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(R.string.search_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (input.toString().equals(""))
                            dialog.cancel();
                        else
                            handleSearch(input.toString());

                    }
                }).show();
    }

    private void handleSearch(String query) {
        bookISBN = query;

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);

        } else Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public Loader<Book> onCreateLoader(int i, Bundle bundle) {

        Uri baseUri = Uri.parse(GOOGLE_BOOK_API_URL + bookISBN);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<Book> loader, Book book) {

        if (book.isGoogleBookAvailable()) {
            Intent intent = new Intent(ScannerActivity.this, EditorActivity.class);
            intent.putExtra("title", book.getTitle());
            intent.putExtra("authors", book.getAuthors());
            intent.putExtra("categories", book.getCategories());
            intent.putExtra("pages", String.valueOf(book.getPageCount()));
            intent.putExtra("imageLink", book.getImageLink());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "No info about this book, ISBN: " + bookISBN + "\nAdd book manually.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Book> loader) {
    }

}
