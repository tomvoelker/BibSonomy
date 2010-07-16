package org.bibsonomy.lucene;

import org.bibsonomy.lucene.util.JNDITestDatabaseBinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author dzo
 * @version $Id$
 */
public abstract class LuceneTest {

	/**
	 * binds bibsonomy_lucene context
	 */
	@BeforeClass
	public static void bind() {
		JNDITestDatabaseBinder.bind();
	}
	
	/**
	 * unbinds bibsonomy_lucene context
	 */
	@AfterClass
	public static void unbind() {
		JNDITestDatabaseBinder.unbind();
	}
}
