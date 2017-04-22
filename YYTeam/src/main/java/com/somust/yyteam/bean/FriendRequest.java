package com.somust.yyteam.bean;

public class FriendRequest {
      
        private String requestPhone;
        private String receivePhone;
        private String friendState;

        public String getRequestPhone() {
                return requestPhone;
        }

        public void setRequestPhone(String requestPhone) {
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

        public FriendRequest(String requestPhone, String receivePhone, String friendState) {
                this.requestPhone = requestPhone;
                this.receivePhone = receivePhone;
                this.friendState = friendState;
        }
}
