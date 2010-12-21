package org.bibsonomy.webapp.util.spring.factorybeans;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;


/**
 * @author rja
 * @version $Id$
 */
public class NullFactoryBeanTest {

	@Test
	public void testSpringInstantiation() throws Exception {
		final Resource res = new FileSystemResource("src/main/webapp/WEB-INF/bibsonomy2-servlet-bibsonomy.xml");
		final BeanFactory factory = new XmlBeanFactory(res);
		
		final Object swordService = factory.getBean("swordService_bibsonomy");
		
		/*
		 * the NullFactoryBean shall return a null object
		 */
		assertNull(swordService);
		
		
	}
	
}
