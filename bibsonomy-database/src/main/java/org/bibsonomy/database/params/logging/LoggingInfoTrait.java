/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
