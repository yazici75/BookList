package com.example.booklist;

//gathering all data related to books in this class


public class Book {

    private String mTitle;
    private int mPageCount;
    private double mPrice;
    private String mCurrency;
    private String mUrl;
    private String mPreviewUrl;

    public Book(String title, int pageCount, double price, String currency, String url, String previewUrl){

        mTitle = title;
        mPageCount = pageCount;
        mPrice = price;
        mCurrency = currency;
        mUrl = url;
        mPreviewUrl = previewUrl;
    }


    public String getTitle(){

        return mTitle;
    }


    public int getPageCount(){

        return mPageCount;
    }

    public double getPrice(){

        return mPrice;
    }

    public String getCurrency(){
        return mCurrency;
    }

    public String getUrl(){
        return mUrl;
    }

    public String getPreviewUrl(){

        return mPreviewUrl;
    }

}
