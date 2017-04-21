package com.somust.yyteam.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

public class TeamNews implements Serializable{
       
        private Integer newsId;
        private Team teamId;
        private String newsTitle;
        private String newsTime;
        private String newsContent;
        private String newsImage;





        public Integer getNewsId() {
                return newsId;
        }
        public void setNewsId(Integer newsId) {
                this.newsId = newsId;
        }
        public Team getTeamId() {
                return teamId;
        }
        public void setTeamId(Team teamId) {
                this.teamId = teamId;
        }
        
        public String getNewsTitle() {
                return newsTitle;
        }
        public void setNewsTitle(String newsTitle) {
                this.newsTitle = newsTitle;
        }
        public String getNewsTime() {
                return newsTime;
        }
        public void setNewsTime(String newsTime) {
                this.newsTime = newsTime;
        }
        public String getNewsContent() {
                return newsContent;
        }
        public void setNewsContent(String newsContent) {
                this.newsContent = newsContent;
        }
        public String getNewsImage() {
                return newsImage;
        }
        public void setNewsImage(String newsImage) {
                this.newsImage = newsImage;
        }




        @Override
        public String toString() {
                return "TeamNews{" +
                        "newsId=" + newsId +
                        ", teamId=" + teamId.toString() +
                        ", newsTitle='" + newsTitle + '\'' +
                        ", newsTime='" + newsTime + '\'' +
                        ", newsContent='" + newsContent + '\'' +
                        ", newsImage='" + newsImage + '\'' +
                        '}';
        }
}
