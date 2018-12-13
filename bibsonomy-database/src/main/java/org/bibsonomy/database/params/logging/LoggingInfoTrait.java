package org.bibsonomy.database.params.logging;

import org.bibsonomy.database.common.enums.LogReason;
import org.bibsonomy.model.User;

import java.util.Calendar;
import java.util.Date;

/**
 * Provides basic logging information.
 * @author ada
 */
public abstract class LoggingInfoTrait {

	private final User loggedUser;
	private final Date loggedTimestamp;
	private final LogReason logReason;

	/**
	 * Initializes both fields with the given values.
	 *
	 * @param loggedUser      the user who caused the entry to the log.
	 * @param loggedTimestamp the time when this entry occurred.
	 * @param logReason
	 */
	public LoggingInfoTrait(User loggedUser, Date loggedTimestamp, LogReason logReason) {
		this.loggedUser = loggedUser;
		this.loggedTimestamp = loggedTimestamp;
		this.logReason = logReason;
	}

	/**
	 * Initializes <code>loggedUser</code> with the given user and <code>loggedTimestampe</code> with the current date.
	 *
	 * @param loggedUser the user who caused the entry to the log.
	 * @param logReason the reason why the entity was logged
	 */
	public LoggingInfoTrait(User loggedUser, LogReason logReason) {
		this(loggedUser, Calendar.getInstance().getTime(), logReason);
	}

	/**
	 * @return the loggedUser
	 */
	public User getLoggedUser() {
		return loggedUser;
	}

	/**
	 * @return the loggedTimestamp
	 */
	public Date getLoggedTimestamp() {
		return loggedTimestamp;
	}

	/**
	 * @return the logReason
	 */
	public LogReason getLogReason() {
		return logReason;
	}
}
