/*
 * Created on 13.05.2007
 */
package org.bibsonomy.common.enums;

public enum GroupID {
	/* constant group ids */
	GROUP_INVALID(-1),
	GROUP_PUBLIC(0),
	GROUP_PRIVATE(1),
	GROUP_FRIENDS(2),
	GROUP_KDE(3);
	
	private final int id;

	private GroupID(final int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}
}
