package org.bibsonomy.recommender.testutil;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author dzo
 * @version $Id$
 */
public final class RecommenderTestContext {
	
	private static final String CONFIG_LOCATION = "TestRecommenderContext.xml";
	
	/** bean factory */
	private static final BeanFactory beanFactory = new ClassPathXmlApplicationContext(CONFIG_LOCATION);
	
	/**
	 * @return the beanfactory for the bibsonomy modul
	 */
	public static BeanFactory getBeanFactory() {
		return beanFactory;
	} 
}
