/**
 *
 *  BibSonomy-Rest-Client - The REST-client.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

// TODO: as long as the rest-server module isn't published we can not add a dependency to the module
//package org.bibsonomy.rest.client;
//
//import static org.junit.Assert.assertEquals;
//
//import org.bibsonomy.model.Bookmark;
//import org.bibsonomy.model.Post;
//import org.bibsonomy.rest.database.TestDBLogic;
//import org.bibsonomy.rest.testutil.TestServerBuilder;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.mortbay.jetty.Server;
//
///**
// * TODO: add more tests here or add a test file for each query
// * 
// * @author dzo
// * @version $Id$
// */
//public class RestLogicTest {
//	private static final int PORT = 8564;
//	private static final String USER_NAME = "hotho";
//	
//	private static String apiUrl;
//	private static Server server;
//	
//	/**
//	 * starts the rest server
//	 * @throws Exception
//	 */
//	@BeforeClass
//	public static void startServer() throws Exception {
//		final TestServerBuilder builder = new TestServerBuilder();
//		builder.setPort(PORT);
//		server = builder.buildServer();
//		server.start();
//		apiUrl = "http://localhost:" + PORT + "/api";
//	}
//	
//	private RestLogic getRestLogic() {
//		return new RestLogic(USER_NAME, "password", apiUrl);
//	}
//	
//	@Test
//	public void getPostDetails() {
//		@SuppressWarnings("unchecked")
//		final Post<Bookmark> postDetails = (Post<Bookmark>) getRestLogic().getPostDetails("c8aef920a0e5b9f99ac4bd599ca4ac71", USER_NAME);
//		
//		assertEquals(TestDBLogic.SEMINAR_URL, postDetails.getResource().getUrl());
//	}
//	
//	/**
//	 * stops the servlet container
//	 * @throws Exception 
//	 */
//	@AfterClass
//	public static void stopServer() throws Exception {
//		server.stop();
//	}
//}
