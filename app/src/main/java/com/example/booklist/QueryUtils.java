package com.example.booklist;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {

    }

    //making all processes with this method to fetch data and return a list

    public static List<Book> fetchDataFromJSON(String requestUrl){

        URL url = createURL(requestUrl);
        String jsonResponse = null;

        try{
            jsonResponse = makeHTTPRequest(url);

        }
        catch (IOException e){

            Log.e(LOG_TAG, "Problem fetching data");
        }

        return extractFromJson(jsonResponse);

    }

    //change String data type to URL

    private static URL createURL(String stringURL) {

        URL url = null;

        if (stringURL == null) {
            return url;
        }

        try {

            url = new URL(stringURL);

        } catch (MalformedURLException e) {

            Log.e(LOG_TAG, "Problem converting String to URL!");
            return null;
        }

        return url;

    }

    //making HTTP request for getting json data

    private static String makeHTTPRequest(URL url) throws IOException {

        HttpURLConnection urlConnection = null;
        String jsonResponse = "";
        InputStream inputStream = null;

        if (url == null) {
            return jsonResponse;
        }

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }


        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP Request");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;

    }

    //changing InputStream data type to String for using json response

    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

    //extract json data and and fill List<Book> with this data

    public static List<Book> extractFromJson(String jsonResponse) {


        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Book> booksFeature = new ArrayList<>();

        try {
            JSONObject jsonBaseObject = new JSONObject(jsonResponse);
            JSONArray items = jsonBaseObject.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject currentBookV = items.getJSONObject(i);
                JSONObject volumeInfo = currentBookV.getJSONObject("volumeInfo");
                String title = volumeInfo.optString("title");
                int pageCount = volumeInfo.optInt("pageCount");
                String previewLink = volumeInfo.optString("previewLink");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String thumbnail = imageLinks.optString("thumbnail");
                JSONObject currentBookS = items.getJSONObject(i);
                JSONObject saleInfo = currentBookS.getJSONObject("saleInfo");
                JSONObject listPrice = saleInfo.getJSONObject("listPrice");
                double price = listPrice.optDouble("amount");
                String currency = listPrice.getString("currencyCode");

//all data from json response is thrown to Book list, <title, total page, price, currency, thumbnail and previewLink>

                Book books = new Book(title, pageCount, price, currency, thumbnail, previewLink);
                booksFeature.add(books);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem retrieving data from JSON : ");
        }

        return booksFeature;
    }
}


