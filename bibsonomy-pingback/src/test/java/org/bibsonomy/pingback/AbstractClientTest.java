/**
 * BibSonomy Pingback - Pingback/Trackback for BibSonomy.
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
