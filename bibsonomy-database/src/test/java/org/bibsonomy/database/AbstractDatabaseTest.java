package org.bibsonomy.database;

import org.bibsonomy.database.testutil.JNDIBinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author dzo
 * @version $Id$
 */
public abstract class AbstractDatabaseTest {

	protected static ApplicationContext testDatabaseContext;
	
	/**
	 * sets up the database context
	 */
	@BeforeClass
	public static void setupContext() {
		// bind datasource access via JNDI
		JNDIBinder.bind();
		// init SystemTagFactory and chain config
		testDatabaseContext = new ClassPathXmlApplicationContext("TestDatabaseContext.xml");
	}
	
	/**
	 * unbinds jndi
	 */
	@AfterClass
	public static void unbind() {
		JNDIBinder.unbind();
	}
}
