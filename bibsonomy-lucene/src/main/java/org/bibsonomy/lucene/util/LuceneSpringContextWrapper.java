package org.bibsonomy.lucene.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * this class wraps a single application context for the lucene modul
 * 
 * @author fei
 * @version $Id$
 */
public class LuceneSpringContextWrapper {
	private static final String CONFIG_LOCATION = "LuceneContext.xml";
	
	/** bean factory */
	private static final BeanFactory beanFactory = new ClassPathXmlApplicationContext(CONFIG_LOCATION);
	
	/**
	 * @return the beanfactory for the bibsonomy modul
	 */
	public static BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	/**
	 * this method just loads the class to init the static fields
	 */
	public static void init() {
		// just load the class
	}
}
