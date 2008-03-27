package org.bibsonomy.common.enums;

/**
 * Defines different states of a user
 * 
 * @author Stefan Stuetzer
 */
public enum SpamStatus {
	
	/** no spammer, sure, classified by admin */
	NO_SPAMMER(0),

	/** spammer, sure, classified by admin */
	SPAMMER(1),

	/** no spammer, not sure, classified by classifier */
	NO_SPAMMER_NOT_SURE(2),

	/** spammer, not sure, classified by classifier */
	SPAMMER_NOT_SURE(3),

	/** no information about spammer status */
	UNKNOWN(9);

	private int id;

	private SpamStatus(int id) {
		this.id = id;
	}

	public String isSpammer() {
		if (id == NO_SPAMMER.id)
			return "no";
		if (id == SPAMMER.id)
			return "yes";
		return "unknown";
	}
	
	public static boolean isSpammer(SpamStatus status) {
		if (status.equals(SPAMMER) || status.equals(SPAMMER_NOT_SURE))
			return true;		
		return false;		
	}

	public int getId() {
		return this.id;
	}

	public static SpamStatus getStatus(final int id) {
		switch (id) {
		case 0:
			return NO_SPAMMER;
		case 1:
			return SPAMMER;		
		case 2:
			return NO_SPAMMER_NOT_SURE;		
		case 3:
			return SPAMMER_NOT_SURE;
		default:
			return UNKNOWN;
		}
	}
	
	/**
	 * Returns in dependece of the prediction and the current
	 * classifier mode the real spammer state to save
	 * in user table
	 * 
	 * @param status The classifires prediction
	 * @param mode The classifiers mode (Day or Night)
	 * @return real state to save in user table
	 */
	public static SpamStatus getRealSpammerState(SpamStatus status, ClassifierMode mode) {
		if (status.equals(SPAMMER) || status.equals(NO_SPAMMER))
			return status;
		
		if (status.equals(NO_SPAMMER_NOT_SURE) || status.equals(SPAMMER_NOT_SURE)) {
			if (mode.equals(ClassifierMode.DAY))
				return NO_SPAMMER;
			else 
				return SPAMMER;
		}	
		
		return status;		
	}

	@Override
	public String toString() {
		switch (id) {
		case 0:
			return "no spammer";
		case 1:
			return "spammer";		
		case 2:
			return "no spammer, not sure";		
		case 3:
			return "spammer, not sure";
		case 9:
			return "unknown";
		default: return "";			
		}		
	}
}