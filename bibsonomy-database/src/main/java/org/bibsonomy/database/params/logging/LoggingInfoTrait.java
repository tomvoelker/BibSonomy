package org.bibsonomy.database.params.logging;

import org.bibsonomy.model.User;

import java.util.Calendar;
import java.util.Date;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Provides basic logging information.
 */
public abstract class LoggingInfoTrait {

    private final User loggedUser;
    private final Date loggedTimestamp;


    /**
     * Initializes both fields with the given values.
     *
     * @param loggedUser the user who caused the entry to the log.
     * @param loggedTimestamp the time when this entry occurred.
     */
    public LoggingInfoTrait(User loggedUser, Date loggedTimestamp) {
        present(loggedUser);
        present(loggedTimestamp);

        this.loggedUser = loggedUser;
        this.loggedTimestamp = loggedTimestamp;
    }


    /**
     * Initializes <code>loggedUser</code> with the given user and <code>loggedTimestampe</code> with the current date.
     *
     * @param loggedUser the user who caused the entry to the log.
     */
    public LoggingInfoTrait(User loggedUser) {
        this(loggedUser, Calendar.getInstance().getTime());
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public Date getLoggedTimestamp() {
        return loggedTimestamp;
    }
}
