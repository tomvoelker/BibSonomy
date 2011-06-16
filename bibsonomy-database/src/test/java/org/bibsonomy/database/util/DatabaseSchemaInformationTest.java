package org.bibsonomy.database.util;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.database.testutil.JNDIBinder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Comment;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Review;
import org.bibsonomy.model.User;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author dzo
 * @version $Id$
 */
public class DatabaseSchemaInformationTest {
	private static DatabaseSchemaInformation dBSchemaInformation;
	
	/**
	 * sets up the instance
	 */
	@BeforeClass
	public static void setup() {
		JNDIBinder.bind();
		dBSchemaInformation = DatabaseSchemaInformation.getInstance();
	}
	
	/**
	 * tests {@link DatabaseSchemaInformation#getMaxColumnLengthForProperty(Class, String)}
	 */
	@Test
	public void testGetMaxColumnLengthForProperty() {
		assertEquals(45, dBSchemaInformation.getMaxColumnLengthForProperty(BibTex.class, "year"));
		assertEquals(45, dBSchemaInformation.getMaxColumnLengthForProperty(GoldStandardPublication.class, "year"));
		assertEquals(255, dBSchemaInformation.getMaxColumnLengthForProperty(User.class, "realname"));
		assertEquals(255, dBSchemaInformation.getMaxColumnLengthForProperty(Comment.class, "text"));
		assertEquals(255, dBSchemaInformation.getMaxColumnLengthForProperty(Review.class, "text"));
	}
}
