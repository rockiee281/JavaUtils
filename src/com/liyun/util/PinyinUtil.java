package com.liyun.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class PinyinUtil {

	private static HanyuPinyinOutputFormat PINYIN_FORMAT = new HanyuPinyinOutputFormat();
	static {
		PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		PINYIN_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
	}

	/**
	 * 得到汉语拼音
	 * 
	 * @param chinese
	 * @return
	 * 
	 * TODO 针对中国人的姓名特定发音做优化，设计一个map做映射
	 */
	public static String toContactPinyin(char chinese) {
		if (!isChinese(chinese)) {
			return String.valueOf(chinese);
		} else {
			try {
				String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(chinese, PINYIN_FORMAT);
				return pinyinArray[0];
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * 判断是否为中文字符，包括标点符号
	 * 
	 * @param c
	 * @return
	 */
	public static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}
}
