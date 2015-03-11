package org.bibsonomy.common.enums;

/**
 * filter for users
 * 
 * @author dzo
 */
public enum UserFilter implements Filter {
	/** all users, normal users and spammer */
	ALL,
	
	/** only no spammers */
	NO_SPAMMER,
	
	/** exclude special mirror account of DBLP */
	WITHOUT_DBLP,
	
	/** only active users (posting) */
	ACTIVE_USERS,
	
	/** spammers */
	SPAMMER;
}
