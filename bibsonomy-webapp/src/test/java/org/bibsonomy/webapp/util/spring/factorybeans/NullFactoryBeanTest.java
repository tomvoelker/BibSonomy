package org.bibsonomy.webapp.util.spring.factorybeans;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @author rja
 * @version $Id$
 */
public class NullFactoryBeanTest {

	@Test
	public void testSpringInstantiation() throws Exception {
		final ApplicationContext factory = new ClassPathXmlApplicationContext("WEB-INF/bibsonomy2-servlet-bibsonomy.xml");
		final Object swordService = factory.getBean("swordService_bibsonomy");
		
		/*
		 * the NullFactoryBean shall return a null object
		 */
		assertNull(swordService);
	}
	
}
