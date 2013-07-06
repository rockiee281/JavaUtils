package com.liyun.contacts;

import org.junit.Assert;
import org.junit.Test;

public class MyContactsMatchTest {

	@Test
	public void testLCS1() {
		MyContactsMatch m = new MyContactsMatch("zhang chang zhi", "zhcz");
		Assert.assertTrue(m.check());
		m.printMatch();

		m = new MyContactsMatch("zhang chang zhi", "zhcg");
		Assert.assertTrue(!m.check());
		m.printMatch();

		m = new MyContactsMatch("zhang chang zhi", "zhgz");
		Assert.assertTrue(!m.check());
		m.printMatch();

		m = new MyContactsMatch("zhang chang zhi", "zhch");
		Assert.assertTrue(m.check());
		m.printMatch();
		
		m = new MyContactsMatch("zhang chang zhi", "zhz");
		Assert.assertTrue(!m.check());
		m.printMatch();
		
		m = new MyContactsMatch("zhang hang zhi", "zhz");
		Assert.assertTrue(m.check());
		m.printMatch();
	}
}
