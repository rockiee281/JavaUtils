package com.liyun.contacts;

import org.junit.Assert;
import org.junit.Test;

public class MyContactsMatchTest {

	@Test
	public void testLCS1() {
		MyContactsMatch m = new MyContactsMatch("张长志", "zhcz");
		Assert.assertTrue(m.check());
		m.printMatch();

		m = new MyContactsMatch("张长志", "zhcg");
		Assert.assertTrue(!m.check());
		m.printMatch();

		m = new MyContactsMatch("张长志", "zhgz");
		Assert.assertTrue(!m.check());
		m.printMatch();

		m = new MyContactsMatch("张长志", "zhch");
		Assert.assertTrue(m.check());
		m.printMatch();
		
		m = new MyContactsMatch("张长志", "zhz");
		Assert.assertTrue(m.check());
		m.printMatch();
		
		m = new MyContactsMatch("张航志", "zhz");
		Assert.assertTrue(m.check());
		m.printMatch();
		
		m = new MyContactsMatch("张hang志", "zhz");
		Assert.assertTrue(m.check());
		m.printMatch();

		m = new MyContactsMatch("张 hang 志", "zhz");
		Assert.assertTrue(m.check());
		m.printMatch();

		m = new MyContactsMatch("张 hang zhi", "zhz");
		Assert.assertTrue(m.check());
		m.printMatch();
		
		m = new MyContactsMatch("张国荣", "zhangg");
		Assert.assertTrue(m.check());
		m.printMatch();
	}
}
