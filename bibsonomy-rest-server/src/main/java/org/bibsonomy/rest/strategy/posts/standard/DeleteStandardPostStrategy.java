package org.bibsonomy.rest.strategy.posts.standard;

import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.users.DeletePostStrategy;

/**
 * @author dzo
 * @version $Id$
 */
public class DeleteStandardPostStrategy extends DeletePostStrategy {

	/**
	 * 
	 * @param context
	 * @param userName
	 * @param resourceHash
	 */
	public DeleteStandardPostStrategy(Context context, String userName, String resourceHash) {
		super(context, userName, resourceHash);
	}

}
