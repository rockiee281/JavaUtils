package com.liyun.contacts;

import org.junit.Assert;
import org.junit.Test;

public class MyContactsMatchTest {

	@Test
	public void testLCS1() {
		MyContactsMatch m = new MyContactsMatch("张长志");
		Assert.assertTrue(m.check("zhcz"));
		m.printMatch("zhcz");

		Assert.assertTrue(!m.check("zhcg"));
		m.printMatch("zhcg");

		Assert.assertTrue(!m.check("zhgz"));
		m.printMatch("zhgz");

		Assert.assertTrue(m.check("zhch"));
		m.printMatch("zhch");

		Assert.assertTrue(m.check("zhz"));
		m.printMatch("zhz");

		m = new MyContactsMatch("张航志");
		Assert.assertTrue(m.check("zhz"));
		m.printMatch("zhz");

		m = new MyContactsMatch("张hang志");
		Assert.assertTrue(m.check("zhz"));
		m.printMatch("zhz");
		
		m = new MyContactsMatch("张昊");
		Assert.assertTrue(m.check("zh"));
		m.printMatch("zh");
		
		m = new MyContactsMatch("张昊昊");
		Assert.assertTrue(m.check("zh"));
		m.printMatch("zh");
		
		m = new MyContactsMatch("张 hang 志");
		Assert.assertTrue(m.check("zhz"));
		m.printMatch("zhz");

		// TODO this should be matched !
		// Assert.assertTrue(m.check("zhangz"));
		// m.printMatch();

		m = new MyContactsMatch("张 hang zhi");
		Assert.assertTrue(m.check("zhz"));
		m.printMatch("zhz");

		m = new MyContactsMatch("张国荣");
		Assert.assertTrue(m.check("zhangg"));
		m.printMatch("zhangg");

		Assert.assertTrue(!m.check("zhangr"));
		m.printMatch("zhangr");

		Assert.assertTrue(m.check("zhgr"));
		m.printMatch("zhgr");
		
		m = new MyContactsMatch("zhang gao");
		Assert.assertTrue(!m.check("zhangao"));
		m.printMatch("zhangao");
		
		m = new MyContactsMatch("zhang gao ren");
		Assert.assertTrue(!m.check("zhangao"));
		m.printMatch("zhangao");

	}
}
