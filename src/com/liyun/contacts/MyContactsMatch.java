package com.liyun.contacts;

public class MyContactsMatch {
	private String base;

	private String target;
	private String[] baseWords;
	private int[] matchPositions;
	private char[] targetChar;
	private char[][] baseCharArray;

	public MyContactsMatch(String base, String target) {
		this.base = base;
		this.target = target;
		this.baseWords = base.split(" ");
		matchPositions = new int[baseWords.length];
		targetChar = target.toCharArray();
		baseCharArray = new char[baseWords.length][];
		for (int i = 0; i < baseWords.length; i++) {
			baseCharArray[i] = baseWords[i].toCharArray();
		}
	}

	public boolean check() {

		for (int i = 0; i < baseWords.length; i++) {
			if (checkSub(i)) {
				return true;
			}
			matchPositions = new int[baseWords.length];
		}

		return false;
	}

	public void printMatch() {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < baseWords.length; i++) {
			if (matchPositions[i] > 0) {
				sb.append("<m>" + baseWords[i] + "</m> ");
			} else {
				sb.append(baseWords[i] + " ");
			}
		}
		System.out.println(target + "|" + sb);
	}

	private boolean checkSub(int startBaseWordIndex) {

		// System.out.println("target is [" + target + "], start from base in ["
		// + startBIndex + "]");
		int tIndex = 0;
		for (int wordIndex = startBaseWordIndex; wordIndex < baseWords.length; wordIndex++) {

			char[] baseCharOfWord = baseCharArray[wordIndex];
			for (int charIndex = 0; charIndex < baseCharOfWord.length; charIndex++) {
				if (baseCharOfWord[charIndex] == targetChar[tIndex]) {
					tIndex++;
					matchPositions[wordIndex] = 1;
					if (tIndex == targetChar.length) { // match all the target
														// char
						return true;
					}
				} else { // 当前不匹配的情况

					if (startBaseWordIndex == baseWords.length - 1) {
						// 已经到最后一个词，并且未能匹配，则肯定不能匹配
						return false;
					} else if (matchPositions[wordIndex] > 0) { // 当前词已经匹配过，则跳到下个word
						break;
					} else if (tIndex > 0 && targetChar[tIndex - 1] == baseCharArray[startBaseWordIndex + 1][0]) {
						/*
						 * 当前词未匹配，但是后向纠错可以匹配 比如 “zhang hao
						 * feng”在输入“zhf”时，zh会首先匹配到zhang，当f匹配到hao时发生错误
						 * 此时需要将f前面的h做后向纠错以正确的匹配
						 * 此种情况适用于h会出现在zh\ch\sh特殊声母，以及n、g这种会同时出现在声母及韵母的字母
						 */

						if (tIndex > 0 && targetChar[tIndex - 1] == baseCharArray[wordIndex][0]) {
							matchPositions[wordIndex] = 1;
							break;
						}
					} else {
						// 本次匹配失败
						return false; 
					}

				}
			}
		}
		return false;
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

		System.out.println(lcs("zhangchangzhi", "hcz"));
	}

	public static String lcs(String a, String b) {
		int aLen = a.length();
		int bLen = b.length();
		if (aLen == 0 || bLen == 0) {
			return "";
		} else if (a.charAt(aLen - 1) == b.charAt(bLen - 1)) {
			return lcs(a.substring(0, aLen - 1), b.substring(0, bLen - 1)) + a.charAt(aLen - 1);
		} else {
			String x = lcs(a, b.substring(0, bLen - 1));
			String y = lcs(a.substring(0, aLen - 1), b);
			return (x.length() > y.length()) ? x : y;
		}
	}
}
