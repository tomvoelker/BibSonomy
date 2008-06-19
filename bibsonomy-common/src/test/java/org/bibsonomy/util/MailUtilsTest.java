package org.bibsonomy.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @author Robert Jäschke
 * @version $Id$
 */
public class MailUtilsTest {

	private MailUtils mailUtils;
	
	/**
	 * do before testing
	 * @throws IOException 
	 */
	@Before
	public void init() throws IOException {
		final Properties props = new Properties();
		props.load(MailUtilsTest.class.getClassLoader().getResourceAsStream("project.properties"));
		mailUtils = getMailUtils(props);
		
		
	}
	
	private MailUtils getMailUtils(final Properties props) {
		final MailUtils utils = new MailUtils();
		
		utils.setMailHost(props.getProperty("mail.smtp.host"));
		utils.setProjectBlog(props.getProperty("project.blog"));
		utils.setProjectEmail(props.getProperty("project.email"));
		utils.setProjectHome(props.getProperty("project.home"));
		utils.setProjectName(props.getProperty("project.name"));
		utils.setProjectRegistrationFromAddress(props.getProperty("project.registrationFromAddress"));
		
		final ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
		resourceBundleMessageSource.setBasename("messages");
		utils.setMessageSource(resourceBundleMessageSource);
		
		return utils;
	}

	/**
	 * Test, if sending registration mails works.
	 */
	@Test
	//@Ignore
	public void testSendRegistrationMail() {
		try {
			assertTrue(mailUtils.sendRegistrationMail("testuser", "devnull@cs.uni-kassel.de", "255.255.255.255", new Locale("en")));
		} catch (Exception e) {
			fail();
		}
	}

}