package com.mohit.example.dailynews.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohit.example.dailynews.R;
import com.mohit.example.dailynews.models.NewsItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohit Goel on 19-Nov-18.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private List<NewsItem> mNewsItemList = new ArrayList<>();

    public NewsListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.news_list_item, parent, false);

        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bindNews(mNewsItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNewsItemList.size();
    }

    public void resetData() {
        mNewsItemList.clear();
    }
    public void setNews(List<NewsItem> newsItems) {
        mNewsItemList.clear();
        mNewsItemList = newsItems;
        notifyDataSetChanged();
    }


    public class NewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_text_view)
        TextView titleTextView;
        @BindView(R.id.author_name_text_view)
        TextView authorNameTextView;
        @BindView(R.id.section_text_view)
        TextView sectionTextView;
        @BindView(R.id.date_text_view)
        TextView dateTextView;
        @BindView(R.id.time_text_view)
        TextView timeTextView;

        public NewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        private void bindNews(final NewsItem newsItem) {
            titleTextView.setText(newsItem.getTitle());
            authorNameTextView.setText(newsItem.getAuthorName());
            timeTextView.setText(newsItem.getTime());
            sectionTextView.setText(newsItem.getSectionName());
            dateTextView.setText(newsItem.getPublishDate());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri newsUri = Uri.parse(newsItem.getWebUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, newsUri);
                    mLayoutInflater.getContext().startActivity(intent);
                }
            });
        }
    }

}
