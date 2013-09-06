package com.liyun.homelinkSpider;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 解析链家的房产详情页
 * */
public class HomeLinkeDetailPageTask implements Runnable {

	private Element houseElement;
	private ConcurrentLinkedQueue<String> queue;

	public HomeLinkeDetailPageTask(Element houseElement, ConcurrentLinkedQueue<String> queue) {
		this.houseElement = houseElement;
		this.queue = queue;
	}

	@Override
	public void run() {
		Element priceEle = houseElement.select("div.price").first();
		String price = priceEle.select("ul").first().select("b").text();
		float priceVal = Float.valueOf(price);
		if (priceVal > HomeSpider.UPPER_COST) {
			// 总价超了，买不起，放弃…………
			return;
		}
		HouseDetail house = new HouseDetail();
		house.setTotalPrice(priceVal);

		float avgPrice = Float.valueOf(priceEle.select("p").text().replaceAll("[^0-9.]", ""));
		house.setAvgPrice(avgPrice);

		Element title = houseElement.select("h3").first().select("a").first();
		house.setLink(HomeSpider.HOST + title.attr("href"));

		String id = house.getLink().substring(house.getLink().lastIndexOf("/") + 1, house.getLink().lastIndexOf("."));
		house.setId(id);
		house.setTitle(title.text());

		Element content = houseElement.select("div.content").first();
		house.setDistrict(content.select("li.one").first().text());
		house.setApartment(content.select("li.two").first().text());
		String acreage = content.select("li.three").first().text();
		house.setAcreages(Float.valueOf(acreage.replaceAll("[^0-9.]", "")));
		String desc = content.select("p.clearfix").first().html();
		String[] info = desc.split("<br />");
		String[] detail1 = info[0].split(",");
		house.setFloor(detail1[0]);
		house.setDirection(detail1[1]);
		house.setDecoration(detail1[2]);
		String[] detail2 = info[1].split(",");
		house.setBuild(detail2[0]);
		house.setYear(detail2[1]);
		house.setType(detail2[2]);

		Elements specifyInfo = content.select("ol").first().children();
		if (!specifyInfo.isEmpty()) {
			String[] specifyInfoStr = new String[specifyInfo.size()];
			for (int i = specifyInfo.size(); i > 0; i--) {
				specifyInfoStr[i - 1] = specifyInfo.get(i - 1).text();
			}
			house.setSpecifyInfo(specifyInfoStr);
		}
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		try {
			JsonGenerator jsonGenerator = mapper.getJsonFactory().createJsonGenerator(sw);
			jsonGenerator.writeObject(house);
			queue.add(sw.getBuffer().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
