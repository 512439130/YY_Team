package com.somust.yyteam.bean;

import android.graphics.Bitmap;

/**
 * Created by 13160677911 on 2017-4-21.
 */

public class TeamNewsImage {
    private Bitmap newsImage;
    private Bitmap teamImage;
    private Bitmap presidentImage;

    public Bitmap getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(Bitmap newsImage) {
        this.newsImage = newsImage;
    }

    public Bitmap getTeamImage() {
        return teamImage;
    }

    public void setTeamImage(Bitmap teamImage) {
        this.teamImage = teamImage;
    }

    public Bitmap getPresidentImage() {
        return presidentImage;
    }

    public void setPresidentImage(Bitmap presidentImage) {
        this.presidentImage = presidentImage;
    }
}
