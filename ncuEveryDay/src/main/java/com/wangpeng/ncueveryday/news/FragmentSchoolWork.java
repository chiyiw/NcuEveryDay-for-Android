package com.wangpeng.ncueveryday.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wangpeng.ncueveryday.R;

public class FragmentSchoolWork extends mFragmentBase {
    private PullToRefreshListView newslistview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.news_schoolwork, container, false);
        newslistview = (PullToRefreshListView) v.findViewById(R.id.newslistview);
        newslistview.setMode(PullToRefreshBase.Mode.BOTH);

        initView(newslistview, "393", "list3.json");

        return v;
    }
}
