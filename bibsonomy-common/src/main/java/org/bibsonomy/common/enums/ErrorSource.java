/**
 * 
 */
package org.bibsonomy.common.enums;

import org.bibsonomy.common.errors.ErrorMessage;

/**
 * Defines error sources
 * 
 * Helps indentifying where an error occurred e.g. in {@link ErrorMessage}
 * @author sdo
 * @version $Id$
 */


public enum ErrorSource {
//TODO: Think of a useful field to add to each constant	
// e.g. something that helps in the webapp to determine
// how to handle the error
	
/** one of the systemTags resulted in an error */
	SYSTEM_TAG,
/** the logged in user was not allowed to handle this post */
	WRONG_USER,
/** a post was doubled and can therefore not be stored **/
	DUPLICATEPOST,
/** something unspecified happened, try to use an other more specific category if possible**/	
	GENERAL;
}
