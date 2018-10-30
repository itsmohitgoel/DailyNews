package com.mohit.example.dailynews;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
LoaderManager.LoaderCallbacks<List<NewsItem>>{


    private static final int ID_NEWS_ASYNC_LOADER = 1;
    private static final String GUARDIAN_REQUEST_TEST_URL = "https://content.guardianapis.com/search?" +
            "q=debate%20AND%20(economy%20OR%20immigration%20education)" +
            "&tag=politics/politics" +
            "&show-tags=contributor&&from-date=2018-10-01" +
            "&api-key=test";

    private NewsListAdapter mAdapter;

    @BindView(R.id.newslist_recycler_view)
    RecyclerView mNewsListRecyclerView;
    @BindView(R.id.empty_text_view)
    TextView mEmptyTextView;
    @BindView(R.id.loading_progress_bar)
    View mLoadingProgressBar;

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
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {
        NewsAsyncLoader newsAsyncLoader = new NewsAsyncLoader(this, GUARDIAN_REQUEST_TEST_URL);
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
