package com.liyun.contacts;

import org.junit.Assert;
import org.junit.Test;

public class MyContactsMatchTest {

	@Test
	public void testLCS1() {
		MyContactsMatch m = new MyContactsMatch("张长志");
		Assert.assertTrue(m.check("zhcz"));
		m.printMatch();

		Assert.assertTrue(!m.check("zhcg"));
		m.printMatch();

		Assert.assertTrue(!m.check("zhgz"));
		m.printMatch();

		Assert.assertTrue(m.check("zhch"));
		m.printMatch();

		Assert.assertTrue(m.check("zhz"));
		m.printMatch();

		m = new MyContactsMatch("张航志");
		Assert.assertTrue(m.check("zhz"));
		m.printMatch();

		m = new MyContactsMatch("张hang志");
		Assert.assertTrue(m.check("zhz"));
		m.printMatch();

		m = new MyContactsMatch("张 hang 志");
		Assert.assertTrue(m.check("zhz"));
		m.printMatch();

		// TODO this should be matched !
		// Assert.assertTrue(m.check("zhangz"));
		// m.printMatch();

		m = new MyContactsMatch("张 hang zhi");
		Assert.assertTrue(m.check("zhz"));
		m.printMatch();

		m = new MyContactsMatch("张国荣");
		Assert.assertTrue(m.check("zhangg"));
		m.printMatch();

		Assert.assertTrue(!m.check("zhangr"));
		m.printMatch();

		Assert.assertTrue(m.check("zhgr"));
		m.printMatch();
	}
}
