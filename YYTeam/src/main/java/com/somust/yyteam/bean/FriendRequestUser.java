package com.somust.yyteam.bean;

/**
 * 好友请求bean
 */
public class FriendRequestUser {
    private Integer friendrequestId;
    private User requestPhone;
    private String receivePhone;
    private String friendRequestReason;
    private String friendRequestState;

    public FriendRequestUser(User requestPhone, String receivePhone, String friendRequestReason, String friendRequestState) {
        this.requestPhone = requestPhone;
        this.receivePhone = receivePhone;
        this.friendRequestReason = friendRequestReason;
        this.friendRequestState = friendRequestState;
    }

    public Integer getFriendrequestId() {
        return friendrequestId;
    }

    public void setFriendrequestId(Integer friendrequestId) {
        this.friendrequestId = friendrequestId;
    }

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

    public String getFriendRequestReason() {
        return friendRequestReason;
    }

    public void setFriendRequestReason(String friendRequestReason) {
        this.friendRequestReason = friendRequestReason;
    }

    public String getFriendRequestState() {
        return friendRequestState;
    }

    public void setFriendRequestState(String friendRequestState) {
        this.friendRequestState = friendRequestState;
    }

    @Override
    public String toString() {
        return "FriendRequestUser{" +
                "friendrequestId=" + friendrequestId +
                ", requestPhone=" + requestPhone.toString() +
                ", receivePhone='" + receivePhone + '\'' +
                ", friendRequestReason='" + friendRequestReason + '\'' +
                ", friendRequestState='" + friendRequestState + '\'' +
                '}';
    }
}
