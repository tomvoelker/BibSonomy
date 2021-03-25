package org.bibsonomy.database.params.logging;

import java.util.Date;

import org.bibsonomy.database.common.enums.LogReason;
import org.bibsonomy.model.User;

/**
 * @author dzo
 */
public class InsertUserGroupLog extends LoggingInfoTrait {

	private final String userName;

	/**
	 * default constructor
	 *
	 * @param loggedUser
	 * @param loggedTimestamp
	 * @param logReason
	 * @param userName
	 */
	public InsertUserGroupLog(final User loggedUser, final Date loggedTimestamp, final LogReason logReason, final String userName) {
		super(loggedUser, loggedTimestamp, logReason);
		this.userName = userName;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
}
