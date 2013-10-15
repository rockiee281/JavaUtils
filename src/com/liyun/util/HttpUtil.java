/**
 * 
 */
package com.liyun.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 */
public class HttpUtil {

	private static final String DEFAULT_HTTP_CONTENT_CHARSET = "utf-8";

	private final Log logger = LogFactory.getLog(HttpUtil.class);

	/**
	 * http get 请求.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String get(String url) throws Exception {
		return get(url, DEFAULT_HTTP_CONTENT_CHARSET);
	}

	public String get(String url, String charset) throws Exception {
		HttpGet getMethod = new HttpGet(url);
		try {
			getMethod.setHeader(new BasicHeader(CoreProtocolPNames.HTTP_CONTENT_CHARSET, charset));

			ResponseHandler<String> rspHandler = new BasicResponseHandler();
			HttpClient httpclient = HttpConnectionManager.getClient();
			return httpclient.execute(getMethod, rspHandler);
		} catch (Exception e) {
			logger.error("http请求失败: " + url);
			throw e;
		} finally {
			getMethod.releaseConnection();
		}
	}

	/**
	 * http post 提交.
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String post(String url, Map<String, String> params) throws Exception {
		return post(url, params, DEFAULT_HTTP_CONTENT_CHARSET);
	}

	public String post(String url, Map<String, String> params, String charset) throws Exception {
		HttpPost postMethod = new HttpPost(url);
		try {
			postMethod.setHeader(new BasicHeader(CoreProtocolPNames.HTTP_CONTENT_CHARSET, charset));
			if (params != null) {
				List<NameValuePair> vps = new ArrayList<NameValuePair>();
				for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
					String name = iter.next();
					String value = params.get(name);
					vps.add(new BasicNameValuePair(name, value));
				}
				postMethod.setEntity(new UrlEncodedFormEntity(vps));
			}
			HttpClient httpclient = HttpConnectionManager.getClient();
			HttpResponse rsp = httpclient.execute(postMethod);
			if (rsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(rsp.getEntity(), charset);
			}
			logger.error(EntityUtils.toString(rsp.getEntity(), charset));
			throw new Exception("unexcepted status code: " + rsp.getStatusLine().getStatusCode());
		} catch (Exception e) {
			logger.error("http提交失败: " + url);
			throw e;
		} finally {
			postMethod.releaseConnection();
		}
	}

	public String postMsgBody(String url, String msgBody, String charset) throws Exception {
		HttpPost postMethod = new HttpPost(url);
		try {
			postMethod.setHeader(new BasicHeader(CoreProtocolPNames.HTTP_CONTENT_CHARSET, charset));
			if (msgBody != null) {
				postMethod.setEntity(new StringEntity(msgBody, charset));
			}
			HttpClient httpclient = HttpConnectionManager.getClient();
			HttpResponse rsp = httpclient.execute(postMethod);
			if (rsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(rsp.getEntity());
			}
			logger.error(EntityUtils.toString(rsp.getEntity()));
			throw new Exception("unexcepted status code: " + rsp.getStatusLine().getStatusCode());
		} catch (Exception e) {
			logger.error("http提交失败: " + url);
			throw e;
		} finally {
			postMethod.releaseConnection();
		}

	}

	/**
	 * http下载文件.
	 * 
	 * @param url
	 * @param dir
	 * @param filename
	 * @return
	 */
	public String getFile(String url, String dir, String filename) {
		return getFile(url, dir, filename, null);
	}

	/**
	 * http下载文件.
	 * 
	 * @param url
	 * @param dir
	 * @param filename
	 * @return
	 */
	public String getFile(String url, String dir, String filename, String refererUrl) {
		HttpGet getMethod = new HttpGet(url);
		try {
			if (refererUrl != null) {
				getMethod.setHeader(new BasicHeader("Referer", refererUrl));
			}
			HttpClient httpclient = HttpConnectionManager.getClient();
			HttpResponse rsp = httpclient.execute(getMethod);
			if (rsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream is = rsp.getEntity().getContent();
				File filedir = new File(dir);
				if (!filedir.exists()) {
					filedir.mkdir();
				}
				FileOutputStream fos = new FileOutputStream(new File(dir + "/" + filename));
				byte[] b = new byte[1024 * 4];
				int len = 0;
				while ((len = is.read(b)) != -1) {
					fos.write(b, 0, len);
				}

				is.close();
				fos.close();
				return filename;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 并不下载文件，只是跟随跳转获取文件的真实URL
	 * 
	 * @param 原始url
	 * @return file real download address
	 */
	public String getFileInfo(String url) {
		HttpHead headMethod = new HttpHead(url);
		try {
			HttpContext context = new BasicHttpContext();
			HttpClient httpclient = HttpConnectionManager.getClient();
			HttpResponse rsp = httpclient.execute(headMethod, context);
			if (rsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
				HttpHost currentHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				String currentUrl = currentReq.getURI().isAbsolute() ? currentReq.getURI().toString() : currentHost
						.toURI() + currentReq.getURI();
				return currentUrl;
			}

		} catch (IOException e) {
			e.printStackTrace();
			logger.error("连接[" + url + "]失败!", e);
		} finally {
			headMethod.releaseConnection();
		}
		return null;
	}

	/**
	 * 测试被下载文件的名字
	 */
	public String guessFileName(String url) throws Exception {
		HttpClient httpClient = HttpConnectionManager.getClient();
		HttpHead httpHead = new HttpHead(url);
		try {
			HttpResponse response = httpClient.execute(httpHead);
			String contentDisposition = null;
			if (response.getStatusLine().getStatusCode() == 200) {
				// Content-Disposition
				Header[] headers = response.getHeaders("Content-Disposition");
				if (headers.length > 0)
					contentDisposition = headers[0].getValue();
			}
			httpHead.abort();

			if (contentDisposition != null && contentDisposition.startsWith("attachment")) {
				return contentDisposition.substring(contentDisposition.indexOf("=") + 1);
			} else if (Pattern.compile("(/|=)([^/&?]+\\.[a-zA-Z]+)").matcher(url).find()) {
				Matcher matcher = Pattern.compile("(/|=)([^/&?]+\\.[a-zA-Z]+)").matcher(url);
				String s = "";
				while (matcher.find()) {
					// 将最后一个URL上的可能文件名作为本次猜测的结果
					s = matcher.group(2);
				}
				return s;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			httpHead.releaseConnection();
		}
		return "UnknowName.temp";
	}

}
