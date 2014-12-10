/**
 * BibSonomy Pingback - Pingback/Trackback for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.pingback;

import org.junit.After;
import org.junit.Before;
import org.mortbay.jetty.testing.ServletTester;

/**
 * @author rja
 */
public abstract class AbstractClientTest {

	private ServletTester tester;
	protected String baseUrl;
	

	@Before
	public void setUp() throws Exception {
		this.tester = new ServletTester();
		this.tester.setContextPath("/");
		this.tester.addServlet(TestServlet.class, "/*");
		this.baseUrl = tester.createSocketConnector(true);
		this.tester.start();
	}

	@After
	public void shutDown() throws Exception {
		this.tester.stop();
	}
	
}
