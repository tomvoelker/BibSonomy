package org.bibsonomy.database;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author dzo
 * @version $Id$
 */
public abstract class AbstractDatabaseTest {

	public static final ApplicationContext testDatabaseContext = new ClassPathXmlApplicationContext("TestDatabaseContext.xml");
}
