package com.liyun.contacts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.liyun.util.PinyinUtil;

public class MyContactsMatch {
	private String base;

	private String[][] baseWords;
	// private int[] matchWordPositions;
	private Integer[] wordType; // 联系人字符类型,1为中文，否则为非中文
	private char[][][] baseCharArrayPerWord; // 保存联系人按照char拆分之后的信息，中文多音字最多匹配
	private final static int WORDTYPE_CHINESE = 1;
	private final static int WORDTYPE_NO_CHINESE = 0;
	// private int currentMatchCursor; // 保存当前匹配到的单词游标
	// private int tIndex = 0; // 记录用户输入词目前的匹配位置
	private char[] targetChar;

	private Stack<MatchStatus> stack = new Stack<MatchStatus>();

	public MyContactsMatch(String base) {
		this.base = base;
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

				if (pinyin != null) { // 部分中文符号在处理时会返回null
					wordList.add(pinyin);
					wordTypeList.add(WORDTYPE_CHINESE); // 中文
				}
			} else if (tmpCharArray[i] == ' ') {
				// 遇到空格自动分割词
				if (notChineseChar.length() > 0) {
					String[] notChinese = new String[1];
					notChinese[0] = notChineseChar.toString();
					wordList.add(notChinese);
					notChineseChar.delete(0, notChineseChar.length());
					wordTypeList.add(WORDTYPE_NO_CHINESE); // 非中文
				} else {
					continue;
				}
			} else {
				notChineseChar.append(tmpCharArray[i]);
			}

		}

		if (notChineseChar.length() > 0) { // 最后以字母等结尾
			String[] notChinese = new String[1];
			notChinese[0] = notChineseChar.toString();
			wordList.add(notChinese);
			notChineseChar.delete(0, notChineseChar.length());
			wordTypeList.add(WORDTYPE_NO_CHINESE);
		}

		// 得到处理后的word数组以及word类型数组
		this.baseWords = wordList.toArray(new String[wordList.size()][]);
		this.wordType = wordTypeList.toArray(new Integer[wordTypeList.size()]);

		baseCharArrayPerWord = new char[baseWords.length][][];
		for (int i = 0; i < baseWords.length; i++) {
			baseCharArrayPerWord[i] = new char[baseWords[i].length][];
			for (int j = 0; j < baseWords[i].length; j++) {
				baseCharArrayPerWord[i][j] = baseWords[i][j].toCharArray();
			}
		}

	}

	public boolean check(String target) {
		int currentMatchCursor = -1;
		int wordLength = 0;
		targetChar = target.toCharArray();
		for (int i = 0; i < baseWords.length; i++) {
			// 更换匹配方案，清空原有数据
			stack.clear();
			// 初始化匹配status
			MatchStatus matchStatus = new MatchStatus();
			matchStatus.matchWordPositions = new int[base.length()];
			matchStatus.currentMatchCursor = currentMatchCursor;
			matchStatus.tIndex = 0;
			// 压栈
			stack.push(matchStatus);

			if (checkFromWordIndex(i)) {
				return true;
			}
			// matchWordPositions = new int[base.length()];

			wordLength = (wordType[i] == WORDTYPE_NO_CHINESE) ? wordLength + baseWords[i].length : wordLength + 1;
			currentMatchCursor = wordLength - 1;
		}

		return false;
	}

	public void printMatch(String target) {

		StringBuilder sb = new StringBuilder();
		char[] baseChars = base.replaceAll(" ", "").toCharArray();
		for (int i = 0; i < baseChars.length; i++) {
			if (stack.peek().matchWordPositions[i] > 0) {
				sb.append("<m>" + baseChars[i] + "</m> ");
			} else {
				sb.append(baseChars[i] + " ");
			}
		}
		System.out.println(target + "|" + sb.toString());
	}

	/**
	 * 引入最大匹配算法，每次匹配的时候都尝试匹配最多的word， 因为在中文拼音中，h\n字符可以同时出现在当前word的声母和下个word的声母部分，
	 * 因此，每次匹配遇到这两个char的时候都会做最大匹配尝试，check成功则返回当前匹配结果，否则会继续做普通匹配
	 * */
	private boolean checkFromWordIndex(int startBaseWordIndex) {

		for (int wordIndex = startBaseWordIndex; wordIndex < baseWords.length; wordIndex++) {
			char[][] baseCharOfWord = baseCharArrayPerWord[wordIndex];
			int myWordType = wordType[wordIndex];

			// 获取栈顶元素，即当前的匹配方案
			// 记录初始游标以及本词长度
			// 中文长度为1个字符，非中文为字符本身长度
			int initCursor = stack.peek().currentMatchCursor;
			int wordLength = 1;

			boolean checkResult = false;
			if (myWordType == WORDTYPE_NO_CHINESE) {
				// 本词为非中文时的初始化工作
				wordLength = baseCharOfWord[0].length;
				checkResult = checkWord(baseCharOfWord[0], WORDTYPE_NO_CHINESE, wordIndex);
			} else {
				// 本词为中文时的初始化工作
				stack.peek().currentMatchCursor++; // 游标位置退后一位
				int initTIndex = stack.peek().tIndex; // 记录初始的tIndex，方便匹配失败的时候回到初始值
				int tmpMatchCursor = stack.peek().currentMatchCursor;
				for (int multiIndex = 0; multiIndex < baseCharOfWord.length; multiIndex++) {
					char[] charOfWord = baseCharOfWord[multiIndex];
					if (checkWord(charOfWord, WORDTYPE_CHINESE, wordIndex)) {
						checkResult = true;
						// 匹配成功
						break;
					} else {
						// 匹配失败
						stack.peek().tIndex = initTIndex;
						stack.peek().currentMatchCursor = tmpMatchCursor;
					}
				}
			}

			if (!checkResult) {
				return false;
			}

			if (stack.peek().tIndex == targetChar.length) { // match all the
															// target string
				return true;
			}
			stack.peek().currentMatchCursor = initCursor + wordLength;
		}
		return false;
	}

	private boolean checkWord(char[] baseCharOfWord, int wordType, int wordIndex) {
		boolean matchThisWord = false;
		MatchStatus status = stack.peek();
		for (int charIndex = 0; charIndex < baseCharOfWord.length; charIndex++) {
			if (wordType == WORDTYPE_NO_CHINESE) {
				status.currentMatchCursor++;
			}

			if (baseCharOfWord[charIndex] == targetChar[status.tIndex]) {
				if (charIndex == 1 && wordIndex < baseCharOfWord.length - 1) { // 匹配的char非声母第一位,且后面还有待匹配的单词，尝试进行最大匹配
					for (char[] c : baseCharArrayPerWord[wordIndex + 1]) {
						if (c[0] == targetChar[status.tIndex]) {
							// 需要匹配的词和下个单词的第一位声母匹配，进行最大匹配尝试
							MatchStatus maxMatchStatus = (MatchStatus) status.clone();
							stack.push(maxMatchStatus);
							if (checkFromWordIndex(wordIndex + 1)) {
								return true;
							} else {
								stack.pop();
							}
						}
					}
				}

				status.tIndex++;
				matchThisWord = true;
				status.matchWordPositions[status.currentMatchCursor] = 1;
				if (status.tIndex == targetChar.length) { // match all the
															// target
					return true;
				}

				// 如果已经匹配到了最后一个单词的最后一个字符，表示待匹配字符串的长度超过了单词串长度
				if (wordIndex == baseWords.length && charIndex == baseCharOfWord.length - 1) {
					return false;
				}
			} else { // 当前不匹配的情况

				if (wordIndex == baseWords.length - 1) {
					// 已经到最后一个词，并且未能匹配，则肯定不能匹配
					return false;
				} else if (matchThisWord) {
					if (wordType == WORDTYPE_NO_CHINESE) {
						return true;
					}
					// 当前词已经匹配过
					// 需要屏蔽到张国荣 被zhan g r匹配到的情况，如果匹配则必须是全拼匹配或者完整匹配声母
					// 如果是匹配声母则跳到下个字，否则返回false
					if (charIndex == 1) {
						// 匹配首字母
						return true;
					} else if (charIndex == 2 && targetChar[status.tIndex - 1] == 'h') { //
						// 首字母两位的情况
						return true;
					} else {
						return false;
					}

				} else if (status.tIndex > 0 && targetChar[status.tIndex - 1] == baseCharOfWord[charIndex] && targetChar[status.tIndex - 1] == 'h') {
					/*
					 * 当前词未匹配，但是后向纠错可以匹配 比如 “zhang hao
					 * feng”在输入“zhf”时，zh会首先匹配到zhang，当f匹配到hao时发生错误
					 * 此时需要将f前面的h做后向纠错以正确的匹配
					 * 此种情况适用于h会出现在zh\ch\sh特殊声母，以及n、g这种会同时出现在声母及韵母的字母
					 * 考虑到多音字的情形，需要把
					 */

					status.matchWordPositions[status.currentMatchCursor] = 1;
					matchThisWord = true;
					continue;
				} else {
					// 本次匹配失败
					return false;
				}
			}
		}

		return true;
	}

	private boolean isMultiTypeChar(char c) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 用于描述当前匹配方案及其状态的内部类
	 * */
	private class MatchStatus {
		int currentMatchCursor; // 保存当前匹配到的单词游标
		int tIndex = 0; // 记录用户输入词目前的匹配位置
		int[] matchWordPositions;
		int lastMatchCharIndexOfWord; // 上次匹配到的word的位置

		@Override
		protected Object clone() {
			MatchStatus status = new MatchStatus();
			status.tIndex = this.tIndex;
			status.currentMatchCursor = this.currentMatchCursor;
			status.matchWordPositions = Arrays.copyOf(this.matchWordPositions, this.matchWordPositions.length);
			status.lastMatchCharIndexOfWord = this.lastMatchCharIndexOfWord;
			return status;
		}
	}
}
