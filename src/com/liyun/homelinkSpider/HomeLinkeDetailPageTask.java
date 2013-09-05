package com.liyun.homelinkSpider;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 解析链家的房产详情页
 * */
public class HomeLinkeDetailPageTask implements Runnable {

	private HouseDetail house;

	public HomeLinkeDetailPageTask(HouseDetail house) {
		this.house = house;
	}

	@Override
	public void run() {
		System.out.println(ToStringBuilder.reflectionToString(house));
		
	}

}
