package com.example.booklist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(@NonNull Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        View listItemView = convertView;

        if(listItemView == null){

            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        //using Picasso library to download thumbnail from URL

        ImageView book_cover = listItemView.findViewById(R.id.book_cover);
        Picasso.with(getContext()).load(currentBook.getUrl()).into(book_cover);

        //all data from Book class is used to set proper textViews

        TextView book_title = listItemView.findViewById(R.id.book_title);
        book_title.setText(currentBook.getTitle());

        TextView book_page = listItemView.findViewById(R.id.book_page);
        book_page.setText(String.valueOf(currentBook.getPageCount()));

        TextView book_price = listItemView.findViewById(R.id.book_price);
        book_price.setText(String.valueOf(currentBook.getPrice()));

        TextView currencyCode = listItemView.findViewById(R.id.currency);

        //to get proper currency for every country user
        Currency currency = Currency.getInstance(currentBook.getCurrency());
        String currencySymbol = currency.getSymbol();
        currencyCode.setText(currencySymbol);

        return listItemView;


    }
}
