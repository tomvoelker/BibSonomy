package org.bibsonomy.database.params.logging;

import org.bibsonomy.database.common.enums.LogReason;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;

import java.util.Date;

/**
 * parameter class for logging a group membership
 *
 * @author ada
 */
public class InsertGroupMembershipLog extends LoggingInfoTrait {

	private final String username;
	private final Group group;

	public InsertGroupMembershipLog(User loggedUser, Date loggedTimestamp, String username, Group group, LogReason logReason) {
		super(loggedUser, loggedTimestamp, logReason);

		this.group = group;
		this.username = username;
	}

	public InsertGroupMembershipLog(User loggedUser, String username, Group group, LogReason logReason) {
		super(loggedUser, logReason);
		this.group = group;
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public Group getGroup() {
		return group;
	}
}
