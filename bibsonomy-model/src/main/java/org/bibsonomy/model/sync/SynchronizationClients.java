package org.bibsonomy.model.sync;

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
	
	public static SynchronizationClients getById(int id) {
		switch (id) {
		case 0:
			return LOCAL;
		case 1:
			return BIBSONOMY;
		case 2:
			return PUMA;
		default:
			return null;
		}
		
	}
}
