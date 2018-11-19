package com.mohit.example.dailynews;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mohit.example.dailynews.adapters.NewsListAdapter;
import com.mohit.example.dailynews.models.NewsItem;
import com.mohit.example.dailynews.utils.NetworkUtils;
import com.mohit.example.dailynews.utils.NewsAsyncLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<NewsItem>> {


    private static final int ID_NEWS_ASYNC_LOADER = 1;
    private static final String GUARDIAN_REQUEST_BASE_URL = "http://content.guardianapis.com/search";
    private static final String API_KEY_PARAM = "api-key";
    private static final String QUERY_PARAM = "q";
    private static final String AUTHOR_PARAM = "show-tags";
    private static final String API_KEY = BuildConfig.GUARDIAN_API_KEY;
    private static final String AUTHOR_VALUE = "contributor";
    public static final String ORDER_BY_PARAM = "order-by";


    @BindView(R.id.newslist_recycler_view)
    RecyclerView mNewsListRecyclerView;
    @BindView(R.id.empty_text_view)
    TextView mEmptyTextView;
    @BindView(R.id.loading_progress_bar)
    View mLoadingProgressBar;

    private NewsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);
        mNewsListRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        mAdapter = new NewsListAdapter(this);
        mNewsListRecyclerView.setAdapter(mAdapter);

        if (NetworkUtils.isNetworkAvailableAndConnected(this)) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ID_NEWS_ASYNC_LOADER, null, this);
        } else {
            mLoadingProgressBar.setVisibility(View.GONE);
            mEmptyTextView.setText(R.string.heading_no_internet_state);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_menu_item) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return true;
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = Uri.parse(GUARDIAN_REQUEST_BASE_URL).buildUpon();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(getString(R.string.settings_key_order_by),
                getString(R.string.settings_default_order_by));

        String queryText = sharedPrefs.getString(getString(R.string.settings_key_search_for),
                getString(R.string.settings_default_search_for));

        builder.appendQueryParameter(QUERY_PARAM, queryText)
                .appendQueryParameter(ORDER_BY_PARAM, orderBy)
                .appendQueryParameter(AUTHOR_PARAM, AUTHOR_VALUE)
                .appendQueryParameter(API_KEY_PARAM, API_KEY);

        NewsAsyncLoader newsAsyncLoader = new NewsAsyncLoader(this, builder.toString());
        return newsAsyncLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
        mLoadingProgressBar.setVisibility(View.GONE);

        if (newsItems != null && !newsItems.isEmpty()) {
            mAdapter.setNews(newsItems);
            mNewsListRecyclerView.setVisibility(View.VISIBLE);
            mEmptyTextView.setVisibility(View.GONE);
        } else {
            mEmptyTextView.setText(R.string.heading_empty_data_set);
            mEmptyTextView.setVisibility(View.VISIBLE);
            mNewsListRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        mAdapter.resetData();
    }
}
