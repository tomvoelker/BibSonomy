package org.bibsonomy.database.util;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.database.AbstractDatabaseTest;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.User;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class DatabaseSchemaInformationTest extends AbstractDatabaseTest {
	private static DatabaseSchemaInformation dBSchemaInformation;
	
	/**
	 * sets up the instance
	 */
	@BeforeClass
	public static void setup() {
		dBSchemaInformation = testDatabaseContext.getBean(DatabaseSchemaInformation.class);
	}
	
	/**
	 * tests {@link DatabaseSchemaInformation#getMaxColumnLengthForProperty(Class, String)}
	 */
	@Test
	public void testGetMaxColumnLengthForProperty() {
		assertEquals(45, dBSchemaInformation.getMaxColumnLengthForProperty(BibTex.class, "year"));
		assertEquals(45, dBSchemaInformation.getMaxColumnLengthForProperty(GoldStandardPublication.class, "year"));
		assertEquals(255, dBSchemaInformation.getMaxColumnLengthForProperty(User.class, "realname"));
	}
}
