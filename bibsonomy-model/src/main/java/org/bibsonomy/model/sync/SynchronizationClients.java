package org.bibsonomy.model.sync;

/**
 * @author wla
 * @version $Id$
 */
public enum SynchronizationClients {
	/**
	 * bibsonomy as client
	 */
	BIBSONOMY(1),
	
	/**
	 * puma as client TODO is bibsonomy and puma different? 
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
}
