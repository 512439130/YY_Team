package com.somust.yyteam.bean;

public class PersonBean {

	private String Name;
	private String PinYin;
	private String FirstPinYin;
	private String ImageUrl;

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

	public String getImageUrl() {
		return ImageUrl;
	}

	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return "PersonBean{" +
				"Name='" + Name + '\'' +
				", PinYin='" + PinYin + '\'' +
				", FirstPinYin='" + FirstPinYin + '\'' +
				", ImageUrl='" + ImageUrl + '\'' +
				'}';
	}
}
