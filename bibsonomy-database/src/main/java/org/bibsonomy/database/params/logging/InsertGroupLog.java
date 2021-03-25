package org.bibsonomy.database.params.logging;

import org.bibsonomy.database.common.enums.LogReason;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;

import java.util.Date;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Parameters necessary to log a group.
 * @author ada
 */
public class InsertGroupLog extends LoggingInfoTrait {
	private final Group group;

	public InsertGroupLog(User loggedUser, Date loggedTimestamp, Group group, LogReason logReason) {
		super(loggedUser, loggedTimestamp, logReason);
		present(group);
		this.group = group;
	}

	public InsertGroupLog(User loggedUser, Group group, LogReason logReason) {
		super(loggedUser, logReason);
		present(group);
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

}
