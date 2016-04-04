package com.wangpeng.ncueveryday.score;

/**
 * Created by wangpeng on 16/3/31.
 */
public class ScoreResult {
    String course; // 科目
    String score; // 分数
    String credit; // 学分

    public ScoreResult(){}

    public ScoreResult(String course, String score, String credit) {
        this.course = course;
        this.score = score;
        this.credit = credit;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }
}
