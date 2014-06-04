package org.bibsonomy.webapp.util.spring.factorybeans;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

/**
 * @author jensi
 */
public class JsonToMapFactoryBeanTest {
	
	@Test
	public void testMap() throws Exception {
		Map<String, String> map = new JsonToMapFactoryBean("{\"a\": \"b\", \"c\":\"d\"}").getObject();
		assertEquals("b", map.get("a"));
		assertEquals("d", map.get("c"));
	}
	
	@Test
	public void testEmpty() throws Exception {
		Map<String, String> map = new JsonToMapFactoryBean("{}").getObject();
		assertEquals(0, map.size());
	}
}
