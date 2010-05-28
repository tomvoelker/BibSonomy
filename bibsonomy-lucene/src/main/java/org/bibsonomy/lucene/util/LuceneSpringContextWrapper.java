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
	/** bean factory */
	private static final BeanFactory beanFactory = new ClassPathXmlApplicationContext("LuceneContext.xml");
	
	/**
	 * @return the beanfactory for the bibsonomy modul
	 */
	public static BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	/**
	 * TODO
	 */
	public static void init() {
		// just load the class
	}
}
