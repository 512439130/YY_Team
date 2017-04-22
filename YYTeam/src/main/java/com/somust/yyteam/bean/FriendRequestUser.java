package com.somust.yyteam.bean;

public class FriendRequestUser {
        private User requestPhone;
        private String receivePhone;
        private String friendState;

        
        public User getRequestPhone() {
                return requestPhone;
        }
        public void setRequestPhone(User requestPhone) {
                this.requestPhone = requestPhone;
        }
        public String getReceivePhone() {
                return receivePhone;
        }
        public void setReceivePhone(String receivePhone) {
                this.receivePhone = receivePhone;
        }
        public String getFriendState() {
                return friendState;
        }
        public void setFriendState(String friendState) {
                this.friendState = friendState;
        }

}
