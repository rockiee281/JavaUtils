package com.liyun.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtil {

	/**
	 * 将字符串进行gzip编码，并输出到outputstream
	 * 
	 * @throws IOException
	 * */
	public static void gzipEncode(String str, OutputStream out) throws IOException {
		GZIPOutputStream gzipOs = new GZIPOutputStream(out);
		gzipOs.write(str.getBytes("utf-8"));
		gzipOs.flush();
		gzipOs.close();
	}

	/**
	 * 将流中的zip数据进行解码
	 * 
	 * @throws IOException
	 * */
	public static void gzipDecode(InputStream in, OutputStream out) throws IOException {
		GZIPInputStream gzipIn = new GZIPInputStream(in);
		byte[] buffer = new byte[1];
		int len;
		while ((len = gzipIn.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
	}

	public static void gzipDecode(String compressedStr, OutputStream out) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(compressedStr.getBytes("utf-8"));
		GZIPInputStream gzipIn = new GZIPInputStream(in);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = gzipIn.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
	}

	public static void main(String[] args) {
		try {
			// FileOutputStream fos = new FileOutputStream(new File("e:/test"));
			// gzipEncode("我就试试，我不说话", fos);

			// FileInputStream fis = new FileInputStream(new File("e:/test"));
			// gzipDecode(fis, System.out);

			// BufferedReader reader = new BufferedReader(new FileReader(new
			// File("e:/test")));
			// StringBuffer sb = new StringBuffer();
			// String str;
			// while((str = reader.readLine()) != null){
			// System.out.println(str);
			// sb.append(str);
			// }
			// reader.close();
			// gzipDecode(sb.toString(), System.out);

			String test = "\\x1F\\x8B\\x08\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\xAD\\x95\\xDBN\\xDB@\\x10\\x86_%\\xF2u\\x88fO\\xDE]\\xDFUE\\xAA\\x90\\x8AT\\x15\\xF5\\xAA\\xAA\\xD0xw\\x1CV\\xF1!\\xB2\\x1D\\x0E\\xA5\\xBC{7&\\x01\\x02.4\\x04\\xC9W\\xB33\\xDE\\xF9\\xFE\\xF9=\\xBEM\\xD0\\xF5\\xE12\\xF47'u\\xD1$\\xD9\\xCF\\xDB\\x04\\x97\\xCB\\x05\\xDD$Yb\\xB5K\\xAD\\x22\\x99z\\xD2d\\xB5\\xF4H\\xCC3\\xC4\\x5C B\\x8Al\\x92L\\x13\\xBFj\\xB1\\x0FM=\\xA4k\\x1E#\\x1Du]\\x0C\\x9C\\x07\\x1Fc\\xD2\\x17\\xC2\\x18\\xB4\\xBC\\xB0\\xCC{\\x9E\\x9A\\xC2h\\xAD\\x80\\x83\\x11\\xCA(\\xD21\\x7Fs\\x7F\\xA0.\\xE6\\xCF\\xCE\\xCA\\xE0C=\\xFF\\xB4i*\\x9ES\\xED\\xCF\\xABP\\x96a}\\xCE\\x81\\xC1\\x11\\xB0\\xF8L\\xC0d\\xC03\\x051\\xE5\\x92\\xDA\\xEE\\xBE\\x076\\x933\\xC6\\xD6]\\xF4\\xD8\\xF6\\xAF\\xD4IH\\xEE\\xA6{\\xB2\\xEE\\x90\\xE5\\x92\\x9Cb\\x5C\\x10\\x90R\\x02\\x8D\\xE6\\xE0s-\\xAD&\\xB2\\x8A\\x0F=<\\xD1\\x86\\xBD$\\xFD\\x8A\\xAB\\xDA]P;\\x82(\\x8E\\xC0\\x1E\\xB1t\\x02:\\xE3:\\x13\\xE3<#I\\x8F:\\xF0\\xA8\\x03\\xBC\\x03\\xF1\\xE98\\xC1>gfVbN\\x5C\\xA2W6\\x07\\xE4\\x5C\\xE4\\xC8\\x0B`\\x86\\xA4\\x11\\x5C\\xE1\\x0B\\xC6S\\x0C\\xF5I\\xED\\xE9\\xFA\\x95yn9X\\x9A\\x01\\xCB\\x94\\x19\\xE3x\\x8D\\x7FS\\xA7\\x0F\\x82\\xD5\\x22=\\x18\\xF6m\\xEB>k\\xD9\\xBE\\x13\\xD5\\x1Ch]\\xCE\\x0A'U\\xC1\\xE2\\x87\\xA9\\x8C\\xF3\\xDE\\xA6\\x8Er\\xE6\\xC0\\x0AO\\x85\\xD4\\xF8L\\x1A\\xCD\\xE4a\\xAC\\xCCf\\xE2M\\xB0\\x98\\xC4\\xF9G{\\x98\\xC1K\\x13\\x13\\x027\\x1C\\x8D39\\xE7\\x18wQ\\x9E[`\\xDE(\\xA1\\x9C\\x01n\\x0F0\\xB1\\x9E0\\x19\\x9D\\x98q\\xB5\\xDFd\\x1F\\xEAdr\\xF7k\\xBA\\x1F\\xAD+\\x03\\xD5\\xFD1\\xF68,\\xEFe\\x89}\\xD1\\xB4U\\xAC\\xC6\\xDA\\xB7MD\\x9E&U\\xE3W%\\xC5\\xD0\\xE9g\\x80\\xB5\\xC8\\xA1\\xAB\\x9A<\\x94\\xE4\\xE92\\xB8x\\xD0\\xB7+\\xDA\\xF3\\xDE\\x96\\xBA\\xA6\\x5Cm=\\xC2\\xE1\\x9Aq\\xB3f\\xAC\\xA9\\xBFj\\xDAE\\x0C\\xFE\\xA8\\x17usU\\xC7X\\xD3\\x9D?\\xAA!g|6l\\xC7\\xE1\\xEEa$&\\x85\\x94\\xF1\\xF8W\\x10\\xC0\\xA4\\xD9\\xDD\\x00\\x0F\\x1B}y\\xD1\\xC4w\\xDF,c\\xBBl\\x9A\\xF4\\xA1\\xA2\\x7F\\xEC\\xF5ir\\x15\\x8A\\x90d\\x05\\x96\\x1Dm\\xEF\\x89\\xB9\\xC7\\xB8\\xA0\\x92\\xB6\\x12\\xCC\\x97\\xDD\\x96\\xBB\\xC4z\\xBE\\xC2\\xF9:\\xE7\\xF7\\xC5\\xE0\\xB7Q\\x15\\x1D\\xC5\\xB9\\xAD\\xDB\\xB5V\\x7F\\x90\\x88\\x15\\xBAa\\xD3f\\x1A3n\\x86\\x8F;\\xCD\\x08\\xFEK\\xDE/\\xDF\\xBE\\x9F%\\xEB\\xE6\\xD7\\xAF\\x90B\\xA7\\x07\\x0A\\xFD\\xE0\\xD2]\\xA1\\xEF\\xBDSc\\xF5\\xD4?\\x8F\\xEA\\x8Fl\\xEF\\x11\\xF57\\xE5\\xF7\\x13\\xF832\\x82\\xCA\\xB9\\xAA\\x1E@R\\x18\\x9A\\xD8\\x9DI\\x14\\xBB\\xEA\\xC2\\xE68\\x0A\\xC1\\x14\\x13*\\xB5q\\x11\\xFE\\xBA\\xFB\\x0B\\x82\\xAB\\x14\\xF1\\xC2\\x08\\x00\\x00";
			StringBuffer sb = new StringBuffer();
			while (test.length() != 0) {
				String tmp = null;
				if (test.startsWith("\\x")) {
					tmp = test.substring(2, 4);
					sb.append(tmp);
					if (test.length() > 4) {
						test = test.substring(4);
					} else {
						break;
					}
				} else {

					tmp = Integer.toHexString(test.charAt(0));
					sb.append(tmp);
					if (test.length() > 1) {
						test = test.substring(1);
					} else {
						break;
					}
				}
			}
			
			System.out.println(sb);
			
			String testHex = "636f6e74656e743d1fefbfbd0800000000000000";
			sb = new StringBuffer();
			while(testHex.length() > 0){
				String tmp = testHex.substring(0,2);
				int a = Integer.valueOf(tmp, 16);
				sb.append((char)a);
				
				testHex = testHex.substring(2);
			}
			System.out.println(sb);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
