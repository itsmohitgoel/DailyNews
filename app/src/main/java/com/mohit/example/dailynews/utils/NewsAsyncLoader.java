package com.mohit.example.dailynews.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mohit.example.dailynews.models.NewsItem;

import java.util.List;

/**
 * Created by Mohit Goel on 19-Nov-18.
 */

public class NewsAsyncLoader extends AsyncTaskLoader<List<NewsItem>> {
    private final String mUrlString;

    public NewsAsyncLoader(@NonNull Context context, String url) {
        super(context);
        mUrlString = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<NewsItem> loadInBackground() {
        if (mUrlString == null) {
            return null;
        }

        return NetworkUtils.fetchNewsList(mUrlString);
    }
}
