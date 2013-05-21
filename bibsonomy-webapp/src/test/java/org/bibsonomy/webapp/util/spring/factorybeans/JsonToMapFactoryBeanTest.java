package org.bibsonomy.webapp.util.spring.factorybeans;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class JsonToMapFactoryBeanTest {
	@Test
	public void testMap() throws Exception {
		Map<String, String> map = new JsonToMapFactoryBean("{\"a\": \"b\", \"c\":\"d\"}").getObject();
		Assert.assertEquals("b", map.get("a"));
		Assert.assertEquals("d", map.get("c"));
	}
	
	@Test
	public void testEmpty() throws Exception {
		Map<String, String> map = new JsonToMapFactoryBean("{}").getObject();
		Assert.assertEquals(0, map.size());
	}
}
