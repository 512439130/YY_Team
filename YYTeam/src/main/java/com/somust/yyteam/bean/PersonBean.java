package com.somust.yyteam.bean;

public class PersonBean {

	private String Name;
	private String PinYin;
	private String FirstPinYin;

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

	public String toString() {
		return "姓名：" + getName() + "   拼音：" + getPinYin() + "    首字母："
				+ getFirstPinYin();

	}

}
