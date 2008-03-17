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
	/** the member list is public */
	PUBLIC(0),
	/** the member list is hidden */
	HIDDEN(1),
	/** only members can see members */
	MEMBERS(2);

	private final int privlevel;

	private Privlevel(final int privlevel) {
		this.privlevel = privlevel;
	}

	/**
	 * @return constant value behind the symbol
	 */
	public int getPrivlevel() {
		return this.privlevel;
	}

	/**
	 * @param privlevel
	 *            constant value behind the Privlevel symbol to retrieve
	 * @return the corresponding Privlevel-enum for the given int.
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
			// should never happen
			throw new RuntimeException("Privlevel is out of bounds (" + privlevel + ")");
		}
	}
}