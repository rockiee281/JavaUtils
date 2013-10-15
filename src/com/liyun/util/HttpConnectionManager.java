package com.liyun.util;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

/**
 * 
 * @author lx281 http连接池管理
 * */
public class HttpConnectionManager {
	private static PoolingClientConnectionManager cm;
	/**
	 * 最大连接数
	 */
	private final static int MAX_TOTAL_CONNECTIONS = 20;

	/**
	 * 获取连接的最大等待时间
	 */
	private final static int WAIT_TIMEOUT = 60 * 1000;

	/**
	 * 每个路由最大连接数
	 */
	private final static int MAX_ROUTE_CONNECTIONS = 400;

	/**
	 * 连接超时时间
	 */
	private final static int CONNECT_TIMEOUT = 10 * 1000;

	/**
	 * 读取超时时间
	 */
	private final static int READ_TIMEOUT = 10 * 1000;

	private static final String DEFAULT_HTTP_USER_AGENT = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.168 Safari/535.19";
	private static final String DEFAULT_HTTP_CONTENT_CHARSET = "utf-8";

	static { // init connection manager
		cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);
		cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
	}

	/**
	 * 获取http连接
	 * */
	public static HttpClient getClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient(cm);
		HttpParams httpParams = new BasicHttpParams();
		httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT);
		httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, READ_TIMEOUT);
		httpParams.setParameter(CoreProtocolPNames.USER_AGENT, DEFAULT_HTTP_USER_AGENT);
		httpParams.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, DEFAULT_HTTP_CONTENT_CHARSET);
		httpClient.setParams(httpParams);
		httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler());
		httpClient.setRedirectStrategy(new DefaultRedirectStrategy());
		return httpClient;
	}
}