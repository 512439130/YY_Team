package com.somust.yyteam.bean;

public class TeamFriend {
        private Integer friendId;
        private String userId;
        private User friendPhone;
        private String friendRemark;
       
        
       

        public Integer getFriendId() {
                return friendId;
        }


        public void setFriendId(Integer friendId) {
                this.friendId = friendId;
        }


        public String getUserId() {
                return userId;
        }


        public void setUserId(String userId) {
                this.userId = userId;
        }


        public User getFriendPhone() {
                return friendPhone;
        }

        public void setFriendPhone(User friendPhone) {
                this.friendPhone = friendPhone;
        }

        public String getFriendRemark() {
                return friendRemark;
        }


        public void setFriendRemark(String friendRemark) {
                this.friendRemark = friendRemark;
        }


        public TeamFriend(){
                
        }


        public TeamFriend(Integer friendId, String userId, User friendPhone, String friendRemark) {
                this.friendId = friendId;
                this.userId = userId;
                this.friendPhone = friendPhone;
                this.friendRemark = friendRemark;
        }

        @Override
        public String toString() {
                return "TeamFriend{" +
                        "friendId=" + friendId +
                        ", userId='" + userId + '\'' +
                        ", friendPhone=" + friendPhone +
                        ", friendRemark='" + friendRemark + '\'' +
                        '}';
        }
}
