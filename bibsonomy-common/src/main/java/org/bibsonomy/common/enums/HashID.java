package org.bibsonomy.common.enums;

/**
 * @author Jens Illig
 * @version $Id$
 */
public enum HashID {

	/** some special hash. Try to use INTRA_HASH or INTER_HASH instead */
	SIM_HASH0(0),
	/** some special hash. Try to use INTRA_HASH or INTER_HASH instead */
	SIM_HASH1(1),
	/** some special hash. Try to use INTRA_HASH or INTER_HASH instead */
	SIM_HASH2(2),
	/** some special hash. Try to use INTRA_HASH or INTER_HASH instead */
	SIM_HASH3(3),
	/** some special default hash. Try to use INTRA_HASH or INTER_HASH instead */
	SIM_HASH(SIM_HASH1),
	/** hash over more fields */
	INTRA_HASH(SIM_HASH2),
	/** hash over less fields */
	INTER_HASH(SIM_HASH);

	private final int id;

	private HashID(final int id) {
		this.id = id;
	}

	private HashID(final HashID id) {
		this.id = id.getId();
	}

	/**
	 * @return constant value behind the symbol 
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @param simHash constant value of the HashID symbol to retrieve
	 * @return the corresponding simhash.
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
	 * @return an integer array that contains all ids.
	 */
	public static int[] getHashRange() {
		return new int[] { 0, 1, 2, 3 };
	}
}