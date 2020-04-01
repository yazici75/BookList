package com.example.booklist;

import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private BookAdapter arrayAdapter;
    ArrayList<Book> books = new ArrayList<>();
    NetworkInfo networkInfo;
    ListView listView;
    TextView emptyTextView;
    SearchView searchView;
    LoaderManager loaderManager;
    Uri.Builder uriBuilder;
    ObjectAnimator animation;
    View progress_bar;
    private static String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting Status bar color

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        listView = findViewById(R.id.list);
        emptyTextView = findViewById(R.id.emptyState_textView);
        listView.setEmptyView(emptyTextView);

        searchView = findViewById(R.id.search_bar);
        searchView.setIconifiedByDefault(false);

        //String data type is changed to Uri for adding parameters

        Uri baseUri = Uri.parse(REQUEST_URL);
        uriBuilder = baseUri.buildUpon();



            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    //every time user changes text query, network connection is controlled

                    networkInfo = connectivityManager.getActiveNetworkInfo();

                    if(networkInfo != null && networkInfo.isConnected()) {

                        emptyTextView.setText("");

                        //get Metrics is used for animation property translationY, this provides animation works in the same way
                        // on different devices

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                        //this formula is created by trying, developers decide how to create to app's animation
                        float translationY = displayMetrics.heightPixels * (-0.37f);

                        //object animator is used for searchView's translation animation
                        animation = ObjectAnimator.ofFloat(searchView, "translationY", translationY);
                        animation.setDuration(150);
                        animation.start();

                        //when user changes text query every time, this query is added to REQUEST_URL
                        uriBuilder.appendQueryParameter("q", newText);
                        REQUEST_URL = uriBuilder.toString();

                        arrayAdapter = new BookAdapter(MainActivity.this, books);
                        listView.setAdapter(arrayAdapter);

                        loaderManager = getLoaderManager();
                        loaderManager.restartLoader(0, null, MainActivity.this);

                        //avoid repetition query, uriBuilder should be cleaned
                        uriBuilder.clearQuery();

                    }

                    //if no internet connection found
                    else{
                            emptyTextView.setText(R.string.no_internet);
                            listView.setAdapter(null);
                    }

                    return false;
                }
            });

            //setOnItemClickListener for when user clicked list's items, it directs to proper URL

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Book currentBook = arrayAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentBook.getPreviewUrl());

                Intent previewIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(previewIntent);
            }
        });

        progress_bar = (View) findViewById(R.id.progressbar);
    }

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {

        //when processes is in background, progress bar is demonstrated to user
        progress_bar.setVisibility(View.VISIBLE);

        return new BookListLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {

        //when background processed finishes, progress bar disappears
        progress_bar.setVisibility(View.INVISIBLE);

        //clean adapter for using another data in this adapter
        arrayAdapter.clear();

        //if data is null, then search widget goes back first place in layout

        if (data == null) {

            emptyTextView.setText("");
            animation = ObjectAnimator.ofFloat(searchView, "translationY", 0f);
            animation.setDuration(150);
            animation.start();

        }

        //if data is available, it is transferred into adapter

        else if(!data.isEmpty()) {

            emptyTextView.setText("");
            arrayAdapter.addAll(data);
        }

        //if there is no book according to query, it prompts to user "no book found"
        else{
            emptyTextView.setText(R.string.no_data);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {

        arrayAdapter.clear();

    }

}
