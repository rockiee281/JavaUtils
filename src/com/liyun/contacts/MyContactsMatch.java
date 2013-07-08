package com.liyun.contacts;

import java.util.ArrayList;
import java.util.List;

import com.liyun.util.PinyinUtil;

public class MyContactsMatch {
	private String base;

	private String target;
	private String[][] baseWords;
	private int[] matchWordPositions;
	private Integer[] wordType; // 联系人字符类型,1为中文，否则为非中文
	private char[] targetChar;
	private char[][][] baseCharArrayPerWord; // 保存联系人按照char拆分之后的信息，中文多音字最多匹配
	private final static int WORDTYPE_CHINESE = 1;
	private final static int WORDTYPE_NO_CHINESE = 0;
	private int currentMatchCursor = -1; // 保存当前匹配到的单词游标
	private int tIndex = 0; // 记录用户输入词目前的匹配位置

	public MyContactsMatch(String base, String target) {
		this.base = base;
		this.target = target;
		init();
	}

	private void init() {
		// 将中文或中英文混杂的联系人信息转化为拼音
		List<String[]> wordList = new ArrayList<String[]>(20);
		List<Integer> wordTypeList = new ArrayList<Integer>(20);
		StringBuilder notChineseChar = new StringBuilder();
		char[] tmpCharArray = base.toCharArray();
		for (int i = 0; i < tmpCharArray.length; i++) {
			if (PinyinUtil.isChinese(tmpCharArray[i])) {
				String[] pinyin = PinyinUtil.toContactPinyin(tmpCharArray[i]);
				if (notChineseChar.length() > 0) {
					String[] notChinese = new String[1];
					notChinese[0] = notChineseChar.toString();
					wordList.add(notChinese);
					notChineseChar.delete(0, notChineseChar.length());
					wordTypeList.add(WORDTYPE_NO_CHINESE); // 非中文
				}
				wordList.add(pinyin);
				wordTypeList.add(WORDTYPE_CHINESE); // 中文
			} else {
				notChineseChar.append(baseCharArrayPerWord[i]);
			}
		}

		if (notChineseChar.length() > 0) { // 最后以字母等结尾
			String[] notChinese = new String[1];
			notChinese[0] = notChineseChar.toString();
			wordList.add(notChinese);
			notChineseChar.delete(0, notChineseChar.length());
		}

		// 得到处理后的word数组以及word类型数组
		this.baseWords = wordList.toArray(new String[wordList.size()][]);
		this.wordType = wordTypeList.toArray(new Integer[wordTypeList.size()]);

		// 初始化匹配数组
		matchWordPositions = new int[base.length()];

		targetChar = target.toCharArray();
		baseCharArrayPerWord = new char[baseWords.length][][];
		for (int i = 0; i < baseWords.length; i++) {
			baseCharArrayPerWord[i] = new char[baseWords[i].length][];
			for (int j = 0; j < baseWords[i].length; j++) {
				baseCharArrayPerWord[i][j] = baseWords[i][j].toCharArray();
			}
		}

	}

	public boolean check() {

		for (int i = 0; i < baseWords.length; i++) {
			if (checkSub(i)) {
				return true;
			}
			matchWordPositions = new int[baseWords.length];
		}

		return false;
	}

	public void printMatch() {

		StringBuilder sb = new StringBuilder();
		char[] baseChars = base.toCharArray();
		for (int i = 0; i < baseChars.length; i++) {
			if (matchWordPositions[i] > 0) {
				sb.append("<m>" + baseChars[i] + "</m> ");
			} else {
				sb.append(baseChars[i] + " ");
			}
		}
		System.out.println(target + "|" + sb);
	}

	private boolean checkSub(int startBaseWordIndex) {

		tIndex = 0; // 记录用户输入词目前的匹配位置
		for (int wordIndex = startBaseWordIndex; wordIndex < baseWords.length; wordIndex++) {
			char[][] baseCharOfWord = baseCharArrayPerWord[wordIndex];
			int myWordType = wordType[wordIndex];

			// 记录初始游标以及本词长度
			// 中文长度为1个字符，非中文为字符本身长度
			int initCursor = currentMatchCursor;
			int wordLength = 1;

			boolean checkResult = false;
			if (myWordType == WORDTYPE_NO_CHINESE) {
				// 本词为非中文时的初始化工作
				wordLength = baseCharOfWord.length;
				checkResult = checkWord(baseCharOfWord[0], WORDTYPE_NO_CHINESE, wordIndex);
			} else {
				// 本词为中文时的初始化工作
				currentMatchCursor++; // 游标位置退后一位
				int initTIndex = tIndex; // 记录初始的tIndex，方便匹配失败的时候回到初始值
				for (int multiIndex = 0; multiIndex < baseCharOfWord.length; multiIndex++) {
					char[] charOfWord = baseCharOfWord[multiIndex];
					if (checkWord(charOfWord, WORDTYPE_CHINESE, wordIndex)) {
						checkResult = true;
						// 匹配成功
						break;
					} else {
						// 匹配失败
						tIndex = initTIndex;
					}
				}
			}

			if (!checkResult) {
				return false;
			}

			currentMatchCursor = initCursor + wordLength;
		}
		return false;
	}

	private boolean checkWord(char[] baseCharOfWord, int wordType, int wordIndex) {
		boolean matchThisWord = false;

		for (int charIndex = 0; charIndex < baseCharOfWord.length; charIndex++) {
			if (wordType == WORDTYPE_NO_CHINESE) {
				currentMatchCursor++;
			}

			if (baseCharOfWord[charIndex] == targetChar[tIndex]) {
				tIndex++;
				matchThisWord = true;
				matchWordPositions[currentMatchCursor] = 1;
				if (tIndex == targetChar.length) { // match all the target
													// char
					return true;
				}
			} else { // 当前不匹配的情况

				if (wordIndex == baseWords.length - 1) {
					// 已经到最后一个词，并且未能匹配，则肯定不能匹配
					return false;
				} else if (matchThisWord) { // 当前词已经匹配过，则跳到下个word
					return true;
				} else if (tIndex > 0 && targetChar[tIndex - 1] == baseCharArrayPerWord[startBaseWordIndex + 1][0]) {
					/*
					 * 当前词未匹配，但是后向纠错可以匹配 比如 “zhang hao
					 * feng”在输入“zhf”时，zh会首先匹配到zhang，当f匹配到hao时发生错误
					 * 此时需要将f前面的h做后向纠错以正确的匹配
					 * 此种情况适用于h会出现在zh\ch\sh特殊声母，以及n、g这种会同时出现在声母及韵母的字母
					 */

					if (tIndex > 0 && targetChar[tIndex - 1] == baseCharArrayPerWord[wordIndex][0]) {
						matchWordPositions[wordIndex] = 1;
						break;
					}
				} else {
					// 本次匹配失败
					return false;
				}
			}
		}

	}

	public static void main(String[] args) {
		MyContactsMatch m = new MyContactsMatch("zhang chang zhi", "zhcz");
		System.out.println(m.check());
		m.printMatch();

		m = new MyContactsMatch("zhang chang zhi", "zhcg");
		System.out.println(m.check());
		m.printMatch();

		m = new MyContactsMatch("zhang chang zhi", "zhgz");
		System.out.println(m.check());
		m.printMatch();
	}
}
