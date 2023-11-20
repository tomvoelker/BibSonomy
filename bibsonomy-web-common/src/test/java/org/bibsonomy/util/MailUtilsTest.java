/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @author Robert Jäschke
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
	 * @throws Exception 
	 */
	@Test
	@Ignore
	public void testSendRegistrationMail() throws Exception {
		assertTrue(mailUtils.sendRegistrationMail("testuser", "devnull@cs.uni-kassel.de","00000000000000000000000000000000", "255.255.255.255", new Locale("en")));
	}

}