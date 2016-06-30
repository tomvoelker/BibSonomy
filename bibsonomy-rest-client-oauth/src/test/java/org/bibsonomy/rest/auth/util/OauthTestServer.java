/**
 * BibSonomy-Rest-Client-OAuth - The REST-client OAuth Accessor.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.auth.util;

import org.eclipse.jetty.server.Server;

/**
 * server for {@link OAuthTestHandler}
 * 
 * @author dzo
 */
public class OauthTestServer {
	
	/**
	 * run jetty app
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final Server server = new Server(OAuthTestHandler.PORT);
		server.setHandler(new OAuthTestHandler());

		server.start();
		server.join();
	}
}
