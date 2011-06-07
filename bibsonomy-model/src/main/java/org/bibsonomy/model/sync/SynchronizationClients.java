package org.bibsonomy.model.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;

/**
 * @author wla
 * @version $Id$
 */
public enum SynchronizationClients {
	/**
	 * used for test synchronization of 2 accounts on the same system
	 * TODO remove after tests 
	 */
	LOCAL(0),

	/**
	 * bibsonomy as client
	 */
	BIBSONOMY(1),

	/**
	 * puma as client  
	 */
	PUMA(2),

	/**
	 * biblicious as client
	 */
	BIBLICIOUS(3);

	private final int id;

	private SynchronizationClients (final int id) {
		this.id = id;
	}

	/**
	 * @return the id constant behind the symbol
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * FIXME: this data should be stored in the database!
	 * 
	 * @param uri
	 * @return SynchronizationClient
	 */
	public static SynchronizationClients getByUri(final URI uri) {
		if (present(uri)) {
			final String uriString = uri.toString();
			if ("http://puma.uni-kassel.de/".equals(uriString)) {
				return PUMA;
			} 
			if ("http://www.biblicious.org/".equals(uriString)) {
				return BIBLICIOUS;
			} 
			if ("http://www.bibsonomy.org/".equals(uriString)) {
				return BIBSONOMY;
			}
		}
		throw new IllegalArgumentException("Unknown service " + uri);
	}
}
