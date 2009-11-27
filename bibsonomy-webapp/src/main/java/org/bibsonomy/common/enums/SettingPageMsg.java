package org.bibsonomy.common.enums;

/**
 * @author cvo
 * @version $Id$
 */
public enum SettingPageMsg {

	/** no status message available */
	IDLE(0),
	/** password of a user was change successfully */
	PASSWORD_CHANGED_SUCCESS(1),
	/** the current password of a user was entered incorrect*/
	PASSWORD_CURRENT_ERROR(2),
	/** the new and the retyped password do not match */
	PASSWORD_RETYPE_ERROR(3),	

	/** the api key was generate successfully*/
	API_KEY_GENERATED_SUCCESS(4),
	/** the api key was not generate successfully*/
	API_KEY_GENERATED_ERROR(5);
	/**
	 * the relation ID. Mainly used in the table useruser_similarity.
	 */
	final int relationId;
	
	/**
	 * Constructor 
	 * 
	 * @param relationId
	 */
	private SettingPageMsg(final int relationId) {
		this.relationId = relationId;
	}
	
	/**
	 * Get the ID of this user relation.
	 * 
	 * @return - the ID (integer) of this relation
	 */
	public int getId() {
		return this.relationId;
	}
	
	/**
	 * get user relation by its integer ID
	 * 
	 * @param id - the id of the relation
	 * @return the corresponding user relation
	 */
	public static SettingPageMsg getUserRelationById(int id) {
		switch (id) {
			case 0: return SettingPageMsg.IDLE; 
			case 1: return SettingPageMsg.PASSWORD_CHANGED_SUCCESS; 			
			case 2: return SettingPageMsg.PASSWORD_CURRENT_ERROR; 
			case 3: return SettingPageMsg.PASSWORD_RETYPE_ERROR; 
			case 4: return SettingPageMsg.API_KEY_GENERATED_SUCCESS; 
			case 5: return SettingPageMsg.API_KEY_GENERATED_ERROR; 
			default: return SettingPageMsg.IDLE; 
		}		
	}	
}
