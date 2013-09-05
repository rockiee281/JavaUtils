package com.liyun.homelinkSpider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*****************************************************************************
 * 我想有个家 一个不需要太大的地方 养几个小孩，两条狗 有个小花园，或许还能种几棵果树
 ****************************************************************************** 
 * 链家在线的数据准确率很高，参考价值不错，但是它目前的查询功能太弱，比如我想更详细的限制首付、总价、单价来过滤房子
 * 这就更适合我这种第一次买房子，对各个地方的房价不太了解的人群
 * 
 * 所以就开发了这么一个功能，主要是抓取链家在线的页面，抽取其信息保存到lucene索引之中
 * 同时，为了备份也为了便于查询，将检索的信息保存到文本中，方便以后做更多的操作，比如趋势分析等等
 * */
public class HomeSpider {
	// 抓取起始页，链家二手房首页
	private final String START_PAGE_URL = "http://beijing.homelink.com.cn/ershoufang/";
	private final int UPPER_COST = 215; // 能承受的最高总价
	private final Log logger = LogFactory.getLog(getClass());
	private ExecutorService service;
	private final String HOST = "http://beijing.homelink.com.cn/";

	public HomeSpider() {
		service = Executors.newFixedThreadPool(5); // 5个线程抓取数据
	}

	public void crawler() {
		HttpClient httpClient = new DefaultHttpClient();
		String content = getPageContent(httpClient, START_PAGE_URL);

		boolean hasNextPage = true;

		do {
			// 解析页面
			Document doc = Jsoup.parse(content);
			// 处理二手房信息列表
			processList(doc);

			// 判断是否还有更多数据
			Element nextPageSpan = doc.select("div.fanye").first().select("span.nextpage").first();
			hasNextPage = nextPageSpan == null ? false : true; // 还有下一页，表面当前并非最后一页

			if (hasNextPage) {
				String link = nextPageSpan.parent().attr("href");
				if (StringUtils.isEmpty(link)) {
					logger.info("获取下一页连接失败");
					break;
				} else if (link.startsWith("/")) {
					link = HOST + link;
				}
				content = getPageContent(httpClient, link);
			}

		} while (hasNextPage);
		
		try{
			if(service.awaitTermination(1l,TimeUnit.HOURS)){	// 超时时间
				// do something
			}
		}catch (Exception e) {
			logger.error("任务超时", e);
		}
	}

	private String getPageContent(HttpClient httpClient, String url) {
		HttpGet get = new HttpGet(url);
		ResponseHandler<String> rspHandler = new BasicResponseHandler();
		try {
			String content = httpClient.execute(get, rspHandler);
			if (StringUtils.isEmpty(content)) {
				logger.error("抓取页面失败,url=" + START_PAGE_URL);
				return null;
			}
			return content;
		} catch (Exception e) {
			logger.error("抓取页面失败,url=" + START_PAGE_URL, e);
			return null;
		}
	}

	private void processList(Document doc) {
		Elements houseList = doc.getElementById("listData").children();
		for (Element element : houseList) {
			try {
				Element priceEle = element.select("div.price").first();
				String price = priceEle.select("ul").first().select("b").text();
				float priceVal = Float.valueOf(price);
				if (priceVal > UPPER_COST) {
					// 总价超了，买不起，放弃…………
					continue;
				}
				HouseDetail house = new HouseDetail();
				house.setTotalPrice(priceVal);

				float avgPrice = Float.valueOf(priceEle.select("p").text().replaceAll("[^0-9.]", ""));
				house.setAvgPrice(avgPrice);

				Element title = element.select("h3").first().select("a").first();
				house.setLink(HOST + title.attr("href"));
				house.setTitle(title.text());
				
				Element content = element.select("div.content").first();
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
				
				HomeLinkeDetailPageTask task = new HomeLinkeDetailPageTask(house);
				service.execute(task);
			} catch (Exception e) {
				logger.error("处理数据失败，" + element, e);
			}
		}
	}
	
	public static void main(String[] args) {
		HomeSpider spider = new HomeSpider();
		spider.crawler();
	}
}
