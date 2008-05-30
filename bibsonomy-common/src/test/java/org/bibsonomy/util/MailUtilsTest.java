package org.bibsonomy.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.bibsonomy.testutil.JNDITestProjectParams;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class MailUtilsTest {


	/** Test instantiation.
	 * @throws Exception
	 */
	@Test
	public void testInstantiation() throws Exception {
		assertNotNull(MailUtils.getInstance());
		
	}
	
	/**
	 * Test, if sending registration mails works.
	 */
	@Test
	@Ignore
	public void testSendRegistrationMail() {
		JNDITestProjectParams.bind();
		try {
			
			MailUtils utils = MailUtils.getInstance();
			assertTrue(utils.sendRegistrationMail("testuser", "devnull@cs.uni-kassel.de", "255.255.255.255", new Locale("en")));

		} catch (Exception e) {
			fail();
		}
		JNDITestProjectParams.unbind();
	}
	
}
