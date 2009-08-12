package org.bibsonomy.recommender.tags.database;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author fei
 * @version $Id$
 */
public class IdleClosingConnectionManager extends MultiThreadedHttpConnectionManager {
	final Log log = LogFactory.getLog(IdleClosingConnectionManager.class);
	
	@Override
	public void releaseConnection(HttpConnection conn) {
		log.debug("Freeing connection to " + conn.getHost());
		super.releaseConnection(conn);
		log.debug("Closing connection to " + conn.getHost());
		conn.close();
		deleteClosedConnections();
	}

}
