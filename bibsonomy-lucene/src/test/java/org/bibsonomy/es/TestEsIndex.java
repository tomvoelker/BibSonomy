package org.bibsonomy.es;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class TestEsIndex {
	
	private static ESTestClient testClient;
	
	@BeforeClass
	public static void beforeClass() {
		testClient  = (ESTestClient) EsSpringContextWrapper.getBeanFactory().getBean("esClient");
		testClient.createIndex();
		
	}
	
	@AfterClass
	public static void afterClass() {
		testClient.shutdown();
	}
	
	
	@Test
	public void testSomething() {
		
	}
}
