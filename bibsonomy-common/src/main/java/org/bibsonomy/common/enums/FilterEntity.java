package org.bibsonomy.common.enums;

/**
 * Defines possible filter entities
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public enum FilterEntity {

	/**
	 * Use this when you ONLY want to retrieve resources with a PDF
	 * file attached
	 */
	 
	JUST_PDF,
	
	/**
	 * Only retrieve resources which are stored at least two times
	 */
	DUPLICATES,
	
	/**
	 * Filter to retrieve posts for spammers
	 * This can be only used by admins
	 */
	ADMIN_SPAM_POSTS,
	
	/**
	 * Use this when you ONLY want to retrieve resources which are 
	 * viewable for your groups
	 */
	MY_GROUP_POSTS;	
	
	
}