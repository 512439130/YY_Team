package com.somust.yyteam.bean;

import android.graphics.Bitmap;

/**
 * 用户显示好友列表Item的bean
 */
public class PersonBean {

	private String Name;
	private String PinYin;
	private String FirstPinYin;
	private Bitmap Image;

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

	@Override
	public String toString() {
		return "PersonBean{" +
				"Name='" + Name + '\'' +
				", PinYin='" + PinYin + '\'' +
				", FirstPinYin='" + FirstPinYin + '\'' +
				", Image=" + Image +
				'}';
	}
}
