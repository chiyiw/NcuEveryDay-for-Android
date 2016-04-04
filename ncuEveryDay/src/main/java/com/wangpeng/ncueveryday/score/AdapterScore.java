package com.wangpeng.ncueveryday.score;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wangpeng.ncueveryday.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangpeng on 16/3/31.
 */
public class AdapterScore extends BaseAdapter {

    ArrayList<ScoreResult> results;
    Context context;

    public AdapterScore(ArrayList<ScoreResult> results, Context context) {
        this.results = results;
        this.context = context;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.score_result_item, null);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvCourse.setText(results.get(position).getCourse());
        holder.tvScore.setText(results.get(position).getScore());
        holder.tvCredit.setText(results.get(position).getCredit());

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_course) TextView tvCourse;
        @Bind(R.id.tv_score) TextView tvScore;
        @Bind(R.id.tv_credit) TextView tvCredit;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
