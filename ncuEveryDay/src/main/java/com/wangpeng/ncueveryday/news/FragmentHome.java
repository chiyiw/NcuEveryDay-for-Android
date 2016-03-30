package com.wangpeng.ncueveryday.news;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wangpeng.ncueveryday.R;

public class FragmentHome extends mFragmentBase {
    private PullToRefreshListView newslistview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取布局
        View v = inflater.inflate(R.layout.news_home, container, false);
        // 查找到相应的控件
        newslistview = (PullToRefreshListView) v.findViewById(R.id.newslistview);
        newslistview.setMode(PullToRefreshBase.Mode.BOTH);

        initView(newslistview, "397", "list2.json");

        return v;
    }
}
