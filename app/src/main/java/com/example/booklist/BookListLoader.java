package com.example.booklist;

import android.content.Context;
import android.content.AsyncTaskLoader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;


public class BookListLoader extends AsyncTaskLoader<List<Book>>{

    private static String mUrl;

    //background processes here for taking data

    public BookListLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        forceLoad();
    }

    @Nullable
    @Override
    public List<Book> loadInBackground() {
        if(mUrl == null){
            return null;
        }


        return QueryUtils.fetchDataFromJSON(mUrl);
    }

}
