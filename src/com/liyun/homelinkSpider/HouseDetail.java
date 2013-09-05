package com.liyun.homelinkSpider;

public class HouseDetail {
	private float totalPrice; // 总价
	private float avgPrice; // 均价
	private String district; // 小区
	private String title;
	private String type; // 板楼、塔楼
	private String direction; // 朝向
	private String link; // 链家官网连接
	private String[] comments; // 中介评价
	private String year; // 年份
	private String apartment; // 户型
	private float acreages; // 面积
	private String floor;// 楼层
	private String decoration; // 装修
	private String build; // 所处楼

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getDecoration() {
		return decoration;
	}

	public void setDecoration(String decoration) {
		this.decoration = decoration;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public float getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(float avgPrice) {
		this.avgPrice = avgPrice;
	}

	public float getAcreages() {
		return acreages;
	}

	public void setAcreages(float acreages) {
		this.acreages = acreages;
	}

	public void setAcreages(int acreages) {
		this.acreages = acreages;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getApartment() {
		return apartment;
	}

	public void setApartment(String apartment) {
		this.apartment = apartment;
	}

	public float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setAvgPrice(int avgPrice) {
		this.avgPrice = avgPrice;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String[] getComments() {
		return comments;
	}

	public void setComments(String[] comments) {
		this.comments = comments;
	}

}
