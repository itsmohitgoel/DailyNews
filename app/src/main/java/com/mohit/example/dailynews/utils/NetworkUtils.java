package com.mohit.example.dailynews.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;

import com.mohit.example.dailynews.models.NewsItem;

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
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Mohit Goel on 19-Nov-18.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final short RESPONSE_OK = 200;

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL buildURL(String newsUrlString) {
        URL newsUrl = null;
        try {
            newsUrl = new URL(newsUrlString);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Url not built correctly", e);
        }
        return newsUrl;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            if (urlConnection.getResponseCode() == RESPONSE_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "JSON result cannot be retrieved", e);
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

    /**
     * Convert the inputstream into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Returns list of NewsItem after querying the GUARDIAN API
     */

    public static List<NewsItem> fetchNewsList(String requestUrl) {
        URL url = buildURL(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "HTTP REQUEST format is not properly build", e);
        }

        return extractNewsData(jsonResponse);
    }

    /**
     * Return a list of NewsItem objects that has been built up from
     * parsing a JSON response.
     */
    private static List<NewsItem> extractNewsData(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<NewsItem> news = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(newsJSON);

            JSONObject jsonObject = root.getJSONObject("response");
            JSONArray resultsJSONArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsJSONArray.length(); i++) {
                JSONObject currentNews = resultsJSONArray.getJSONObject(i);

                String webTitle = currentNews.optString("webTitle");
                String sectionName = currentNews.optString("sectionName");
                String webPublicationDate = currentNews.optString("webPublicationDate");


                String[] splitDate = webPublicationDate.split("T");
                String date = splitDate[0];
                StringBuilder time = new StringBuilder(splitDate[1]);

                JSONArray tags = currentNews.optJSONArray("tags");
                String authorName = "";
                if (tags != null && tags.length() > 0) {
                    JSONObject authorProfile = (JSONObject) tags.get(0);
                    authorName = authorProfile.optString("webTitle");
                }
                String url = currentNews.getString("webUrl");

                NewsItem newsItem = new NewsItem();
                newsItem.setTitle(webTitle);
                newsItem.setSectionName(sectionName);
                newsItem.setPublishDate(date);
                newsItem.setWebUrl(url);
                newsItem.setTime(time.toString());
                newsItem.setAuthorName(authorName);

                news.add(newsItem);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception while parsing NEWS JSON results", e);
        }

        return news;

    }

    /**
     * Checks if the Internet connection is available.
     *
     * @param context
     * @return Returns true if the Internet connection is available and connected.
     * False otherwise.
     */
    public static boolean isNetworkAvailableAndConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = connectivityManager.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                connectivityManager.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

}
