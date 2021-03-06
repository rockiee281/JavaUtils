package com.liyun.homelinkSpider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
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
	public final static int UPPER_COST = 215; // 能承受的最高总价
	private final Log logger = LogFactory.getLog(getClass());
	private ExecutorService processService;
	private ExecutorService crawlerService;
	public final static String HOST = "http://beijing.homelink.com.cn/";
	private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
	private final int crawlerThreadCount = 3;
	private final int processThreadCount = 1;
	private final int startPage = 5530; // 抓取起始页

	public HomeSpider() {
		crawlerService = Executors.newFixedThreadPool(crawlerThreadCount); // 爬虫线程池
		processService = Executors.newFixedThreadPool(processThreadCount); // 线程处理数据
	}

	public void crawler() {
		// 启动文件写入线程
		WriteFileThread writeFile = new WriteFileThread();
		Thread thread = new Thread(writeFile);
		thread.setName("file-writer-thread");
		thread.start();

		for (int i = 0; i < crawlerThreadCount; i++) {
			InnerSpider spider = new InnerSpider(startPage + i, crawlerThreadCount, i);
			crawlerService.execute(spider);
		}

		try {
			crawlerService.shutdown(); // 发出关闭抓取线程池信号
			if (crawlerService.awaitTermination(1l, TimeUnit.HOURS)) { // 等待所有抓取线程结束运行
				logger.info("抓取线程运行结束");
				processService.shutdown(); // 关闭分析线程池，会在执行所有任务之后关闭
				if (processService.awaitTermination(1l, TimeUnit.HOURS)) { // 等待数据处理结束
					logger.info("分析线程运行结束");
					// 停止文件写入线程
					writeFile.stop();
				}
			}
		} catch (Exception e) {
			logger.error("任务超时", e);
		}
	}

	private String getPageContent(HttpClient httpClient, String url) {
		HttpGet get = new HttpGet(url);
		setCommonRequestHeader(get);
		ResponseHandler<String> rspHandler = new BasicResponseHandler();
		try {
			String content = httpClient.execute(get, rspHandler);
			logger.info("get page : " + url);

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
				HomeLinkeDetailPageTask task = new HomeLinkeDetailPageTask(element, queue);
				processService.execute(task);
			} catch (Exception e) {
				logger.error("处理数据失败，" + element, e);
			}
		}
	}

	public static void main(String[] args) {
		HomeSpider spider = new HomeSpider();
		spider.crawler();
	}

	/**
	 * 设置通用header
	 * */
	private void setCommonRequestHeader(HttpGet method) {
		method.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		method.addHeader("Connection", "keep-alive");
		method.addHeader("Cache-Control", "max-age=0");
		method.addHeader("Referer", "http://beijing.homelink.com.cn/ershoufang/");
		method.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.62 Safari/537.36");
	}

	/**
	 * 内部类，用于写入文件
	 * */
	class WriteFileThread implements Runnable {
		private boolean hasMore = true;
		private BufferedWriter out;

		@Override
		public void run() {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String today = sdf.format(new Date());
				out = new BufferedWriter(new FileWriter(new File(today)));
				File dir = new File("homelinkData");
				if (!dir.exists()) {
					dir.mkdir();
				}
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(new File("homelinkData/" + today)), "utf-8"));
			} catch (Exception e) {
				logger.error("创建文件失败", e);
				return;
			}

			while (hasMore) {
				if (!queue.isEmpty()) {
					try {
						out.append(queue.poll() + "\n");
					} catch (IOException e) {
						logger.error("写入文件失败", e);
					}
				}

				try {
					Thread.sleep(1 * 1000);// 休眠1s
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void stop() {
			this.hasMore = false;
		}
	}

	/**
	 * 内部类，用于爬取内容
	 * */
	class InnerSpider extends Thread {
		private int pageNum;
		private int mod;

		public InnerSpider(int startPage, int mod, int index) {
			setName("crawler-thread-" + index);
			this.pageNum = startPage;
			this.mod = mod;
		}

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			String link = START_PAGE_URL + "pg" + pageNum + "/";
			String content = getPageContent(httpClient, link);
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
					pageNum += mod;
					link = START_PAGE_URL + "pg" + pageNum + "/";
					content = getPageContent(httpClient, link);
				} else {
					logger.info(link);
					logger.info("抓取结束，么有更多数据了，当前是[" + pageNum + "]页");
				}
				try {
					Thread.sleep(1000 * 5); // 抓太快容易出问题，每次休息5s好了
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} while (hasNextPage);

		}

	}
}
