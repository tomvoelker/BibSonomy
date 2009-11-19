package org.bibsonomy.lucene.util;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * this class wraps a single application context for the lucene modul
 * 
 * @author fei
 */
public class LuceneSpringContextWrapper {
	/** bean factory */
	private static BeanFactory beanFactory;

	/**
	 * static initialization
	 */
	static {
		ApplicationContext context = new ClassPathXmlApplicationContext(
		        new String[] {"LuceneContext.xml"});

		// an ApplicationContext is also a BeanFactory (via inheritance)
		beanFactory = context;
	}
	
	/**
	 * get the beanfactory for the bibsonomy modul
	 * @return
	 */
	public static BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
