package com.wangpeng.ncueveryday.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wangpeng.ncueveryday.R;

public class FragmentCollege extends mFragmentBase {

    private PullToRefreshListView newslistview; // 新闻列表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_college, container, false);

        newslistview = (PullToRefreshListView) v
                .findViewById(R.id.newslistview);
        newslistview.setMode(Mode.BOTH);

        initView(newslistview, "394", "list1.json");

        return v;
    }
}
