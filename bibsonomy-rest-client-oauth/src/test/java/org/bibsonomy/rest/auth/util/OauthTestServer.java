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
