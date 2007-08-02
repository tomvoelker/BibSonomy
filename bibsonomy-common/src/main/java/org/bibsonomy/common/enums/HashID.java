package org.bibsonomy.common.enums;

/**
 * @author Jens Illig
 * @version $Id$
 */
public enum HashID {

	SIM_HASH0(0),
	SIM_HASH1(1),
	SIM_HASH2(2),
	SIM_HASH3(3),
	SIM_HASH(SIM_HASH1),
	INTRA_HASH(SIM_HASH2),  
	INTER_HASH(SIM_HASH);

	private final int id;

	private HashID(final int id) {
		this.id = id;
	}

	private HashID(final HashID id) {
		this.id = id.getId();
	}

	public int getId() {
		return this.id;
	}

	/**
	 * Returns the corresponding simhash.
	 */
	public static HashID getSimHash(final int simHash) {
		switch (simHash) {
		case 0:
			return SIM_HASH0;
		case 1:
			return SIM_HASH1;
		case 2:
			return SIM_HASH2;
		case 3:
			return SIM_HASH3;
		default:
			throw new RuntimeException("SimHash " + simHash + " doesn't exist.");
		}
	}

	/**
	 * Returns an integer array that contains all ids.
	 */
	public static int[] getHashRange() {
		return new int[] { 0, 1, 2, 3 };
	}
}