package com.liyun.lbs;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import com.liyun.util.HttpUtil;

/**
 * 用于定位的工具类，可以通过手机号、IP地址、经纬度、基站数据进行定位
 * 
 * @author lx281
 * */
public class LBSUtil {
	private final static Log logger = LogFactory.getLog(LBSUtil.class);

	/**
	 * 根据IP地址查询地理位置
	 * */
	@SuppressWarnings("unchecked")
	public static Location getLocByIP(HttpUtil httpUtil, String ip) {

		if (StringUtils.isEmpty(ip)) {
			return null;
		}

		Location loc = new Location();
		// 调用淘宝的数据接口进行查询
		/** 淘宝提供的IP地址查询接口 */
		String IP_QUERY_TAOBAO = "http://ip.taobao.com/service/getIpInfo.php?ip=";

		try {
			String result = httpUtil.get(IP_QUERY_TAOBAO + ip);
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> ipInfo = mapper.readValue(result, HashMap.class);
			if ("0".equals(ipInfo.get("code").toString())) { // 0 为查询成功
				HashMap<String, Object> ipData = (HashMap<String, Object>) ipInfo.get("data");
				String countryID = (String) ipData.get("country_id");
				if (StringUtils.isEmpty(countryID) || "IANA".equals(countryID)) {
					return null; // 非法的IP地址
				}

				// 国家ID非空并且非保留地址
				loc.setCountry((String) ipData.get("country"));
				loc.setRegion((String) ipData.get("region"));
				loc.setCity((String) ipData.get("city"));
				return loc;
			}

		} catch (Exception e) {
			logger.error("获取IP信息失败,ip=" + ip, e);
			return null;
		}
		return null;
	}

	/**
	 * 根据手机号码定位 src/main/resource/lbs目录下有中国手机号归属地数据库以及一个查询的工具
	 * 从客户端获取用户手机号码的前7位，可以用于定位
	 * */
	// public static Location getLocByPhoneNum(String phoneNum,
	// ChinaMobileRegionDAO dao) {
	//
	// if (StringUtils.isEmpty(phoneNum)) {
	// return null;
	// }
	//
	// ChinaMobileRegion region = new ChinaMobileRegion();
	// region.setNum(phoneNum);
	// region = dao.select(region);
	// if (region != null) {
	// Location loc = new Location();
	// loc.setCountry("中国");
	// loc.setRegion(region.getRegion());
	// loc.setCity(region.getCity());
	// return loc;
	// }
	//
	// return null;
	//
	// }

	/**
	 * 根据经纬度数据获取地理位置
	 * */
	@SuppressWarnings("unchecked")
	public static Location getLocByLatlng(HttpUtil httpUtil, String latitude, String longitude) {
		// google geo api
		// referer : https://developers.google.com/maps/documentation/geocoding/
		// 官方说的是限制为2500/day,不能连续90天超过这个限制
		String googleGeoUrl = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true&language=zh-CN&latlng=";

		if (StringUtils.isEmpty(latitude) || StringUtils.isEmpty(longitude)) {
			return null;
		}
		try {
			String result = httpUtil.get(googleGeoUrl + latitude + "," + longitude);
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> locJson = mapper.readValue(result, HashMap.class);
			String status = locJson.get("status").toString();
			if (!status.equals("OK")) {
				logger.error("request google geo api failed,latlng=" + latitude + "," + longitude);
				return null;
			}
			List<HashMap<String, Object>> locResults = (List<HashMap<String, Object>>) locJson.get("results");
			if (locResults.isEmpty()) {
				logger.error("no result response,latlng=" + latitude + "," + longitude);
				return null;
			}
			List<HashMap<String, Object>> addr = (List<HashMap<String, Object>>) locResults.get(0).get(
					"address_components");
			Location loc = new Location();
			for (HashMap<String, Object> addrDetail : addr) {
				String types = addrDetail.get("types").toString();
				if (types.contains("country")) {
					loc.setCountry(addrDetail.get("long_name").toString());
				} else if (types.contains("administrative_area_level_1")) {
					loc.setRegion(addrDetail.get("long_name").toString());
				} else if (types.contains("locality")) {
					loc.setCity(addrDetail.get("long_name").toString());
				}
			}
			return loc;

		} catch (Exception e) {
			logger.error("解析经纬度数据失败,lat=" + latitude + ",lng=" + longitude, e);
		}
		return null;
	}

	/**
	 * 根据基站信息定位地理位置 采用google map的接口，据说是给nokia使用的
	 * */
	public static Location getLocByMobileBase(HttpUtil httpUtil, String mnc, String mcc, String cellid, String lac) {
		String googleMmap = "http://www.google.com/glm/mmap";
		int cellidVal = Integer.valueOf(cellid);
		byte[] postByteArr = sendDataFormat(cellidVal, Integer.valueOf(lac), Integer.valueOf(mnc), Integer.valueOf(mcc));

		// GSM uses 4 digits while UTMS used 6 digits (hex)
		postByteArr[28] = cellidVal > 65536 ? (byte) 5 : (byte) 3;

		HttpClient httpClient = new HttpClient();
		PostMethod post = new PostMethod(googleMmap);
		post.setRequestEntity(new ByteArrayRequestEntity(postByteArr));

		try {
			httpClient.executeMethod(post);
			if (post.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				double[] retArr = getDataFormat(post.getResponseBody());
				return getLocByLatlng(httpUtil, String.valueOf(retArr[0]), String.valueOf(retArr[1]));
			}
		} catch (Exception ex) {
			logger.error("获取location 失败,mcc|mnc|cellid|lac = " + mcc + "|" + mnc + "|" + cellid + "|" + lac, ex);
			return null;
		}
		return null;
	}

	/**
	 * 
	 * 把int转为十六进制，并且格式为8位，前补足0。
	 * 
	 * @param int
	 * @return
	 */
	private static String int2hex(int value) {
		String str = Integer.toHexString(value);
		String strfmt = "00000000";
		return strfmt.substring(0, strfmt.length() - str.length()) + str;
	}

	/**
	 * 
	 * 把16进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return
	 */
	private static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param bArray
	 * @return
	 */
	private static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * 
	 * 发送数据格式化，cid+lac+mnc+mcc 转为byte[]
	 * 
	 * @param int cid, int lac, int mnc, int mcc
	 * @return
	 */
	private static byte[] sendDataFormat(int cid, int lac, int mnc, int mcc) {
		String string1 = "000E00000000000000000000000000001B0000000000000000000000030000";
		String string2 = "FFFFFFFF00000000";
		String retStr = string1 + int2hex(cid) + int2hex(lac) + int2hex(mnc) + int2hex(mcc) + string2;
		return hexStringToByte(retStr.toUpperCase());
	}

	/**
	 * 
	 * 接收数据格式化，截取出lat、lon
	 * 
	 * @param byte[]
	 * @return
	 */
	private static double[] getDataFormat(byte[] byteArr) {
		double[] retArr = new double[2];
		String resHexStr = bytesToHexString(byteArr);
		String latHexStr = resHexStr.substring(14, 22);
		String lonHexStr = resHexStr.substring(22, 30);
		retArr[0] = Integer.parseInt(latHexStr, 16) / 1000000.0;
		retArr[1] = Integer.parseInt(lonHexStr, 16) / 1000000.0;
		return retArr;
	}
}
