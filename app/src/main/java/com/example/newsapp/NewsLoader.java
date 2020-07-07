package com.example.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsData>> {

    private String mURL;

    public NewsLoader(Context context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsData> loadInBackground() {
        if (mURL == null) {
            return null;
        }
        List<NewsData> newsDataList = com.example.newsapp.QueryUtils.fetchNewsData(mURL);
        return newsDataList;
    }
}
