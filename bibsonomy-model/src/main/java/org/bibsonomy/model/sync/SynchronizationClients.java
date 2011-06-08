package org.bibsonomy.model.sync;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URI;

/**
 * @author wla
 * @version $Id$
 */
public enum SynchronizationClients {
	
	TESTCLIENT(0),
	/**
	 * bibsonomy
	 */
	BIBSONOMY(1),

	/**
	 * puma as client  
	 */
	PUMA(2);

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
			if ("http://www.bibsonomy.org/".equals(uriString)) {
				return BIBSONOMY;
			}
			if ("http://www.test.de/".equals(uriString)) {
				return TESTCLIENT;
			}
		}
		throw new IllegalArgumentException("Unknown service " + uri);
	}
}
