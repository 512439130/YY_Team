package com.somust.yyteam.bean;

import android.graphics.Bitmap;

/**
 * 用户显示好友列表Item的bean
 */
public class PersonBean {
	private Integer userId;
	private String Name;
	private String PinYin;
	private String FirstPinYin;
	private Bitmap Image;
	private String Phone;
	private String teamMemberPosition;
	private String taskMemberJoinTime;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPinYin() {
		return PinYin;
	}

	public void setPinYin(String pinYin) {
		PinYin = pinYin;
	}

	public String getFirstPinYin() {
		return FirstPinYin;
	}

	public void setFirstPinYin(String firstPinYin) {
		FirstPinYin = firstPinYin;
	}

	public Bitmap getImage() {
		return Image;
	}

	public void setImage(Bitmap image) {
		Image = image;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getTeamMemberPosition() {
		return teamMemberPosition;
	}

	public void setTeamMemberPosition(String teamMemberPosition) {
		this.teamMemberPosition = teamMemberPosition;
	}

	public String getTaskMemberJoinTime() {
		return taskMemberJoinTime;
	}

	public void setTaskMemberJoinTime(String taskMemberJoinTime) {
		this.taskMemberJoinTime = taskMemberJoinTime;
	}

	@Override
	public String toString() {
		return "PersonBean{" +
				"Name='" + Name + '\'' +
				", PinYin='" + PinYin + '\'' +
				", FirstPinYin='" + FirstPinYin + '\'' +
				", Image=" + Image +
				", Phone='" + Phone + '\'' +
				", teamMemberPosition='" + teamMemberPosition + '\'' +
				'}';
	}
}
