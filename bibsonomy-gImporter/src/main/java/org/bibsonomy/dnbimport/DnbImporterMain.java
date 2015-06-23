package org.bibsonomy.dnbimport;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class DnbImporterMain {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final BeanFactory beanFactory = new ClassPathXmlApplicationContext("org/bibsonomy/dnbimport/dnbImporterContext.xml");
		beanFactory.getBean("genealogie", Runnable.class).run();
	}

}
