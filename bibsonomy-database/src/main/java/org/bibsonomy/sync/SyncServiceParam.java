package org.bibsonomy.sync;

import java.net.URI;

/**
 * @author wla
 * @version $Id$
 */
public class SyncServiceParam {
	final URI uri;
	final Integer id;
	final boolean server;
	
	/**
	 * @return the uri
	 */
	public URI getUri() {
		return this.uri;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return the server
	 */
	public boolean isServer() {
		return this.server;
	}
	
	public boolean getServer() {
		return this.server;
	}

	public SyncServiceParam(URI uri, Integer id, boolean server) {
		this.uri = uri;
		this.id = id;
		this.server = server;
	}
	
}
