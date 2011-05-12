package org.bibsonomy.database.util;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.database.testutil.JNDIBinder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Review;
import org.bibsonomy.model.User;
import org.bibsonomy.services.database.DatabaseSchemaInformation;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class DatabaseSchemaInformationImplTest {
	private static DatabaseSchemaInformation dBSchemaInformation;
	
	/**
	 * sets up the instance
	 */
	@BeforeClass
	public static void setup() {
		JNDIBinder.bind();
		dBSchemaInformation = DatabaseSchemaInformationImpl.getInstance();
	}
	
	/**
	 * tests {@link DatabaseSchemaInformationImpl#getMaxColumnLengthForProperty(Class, String)}
	 */
	@Test
	public void testGetMaxColumnLengthForProperty() {
		assertEquals(45, dBSchemaInformation.getMaxColumnLengthForProperty(BibTex.class, "year"));
		assertEquals(255, dBSchemaInformation.getMaxColumnLengthForProperty(User.class, "realname"));
		assertEquals(255, dBSchemaInformation.getMaxColumnLengthForProperty(Review.class, "text"));
	}
}
