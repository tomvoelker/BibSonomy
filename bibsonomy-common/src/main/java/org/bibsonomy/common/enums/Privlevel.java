package org.bibsonomy.common.enums;

/**
 * Privacy levels for groups:
 * <ul>
 * <li>public: the member list is public</li>
 * <li>hidden: the member list is hidden</li>
 * <li>members: only members can see members</li>
 * </ul>
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public enum Privlevel {

	PUBLIC(0), HIDDEN(1), MEMBERS(2);

	private final int id;

	private Privlevel(final int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	/**
	 * Returns the corresponding Privlevel-enum for the given int.
	 */
	public static Privlevel getPrivlevel(final int privlevel) {
		if (privlevel > 2 || privlevel < 0) throw new RuntimeException("Privlevel is out of bounds (" + privlevel + ")");

		switch (privlevel) {
		case 0:
			return PUBLIC;
		case 1:
			return HIDDEN;
		case 2:
			return MEMBERS;
		default:
			throw new RuntimeException("Privlevel is out of bounds (" + privlevel + ")");
		}
	}
}