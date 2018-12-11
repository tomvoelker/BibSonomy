package org.bibsonomy.database.params.logging;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;

import java.util.Date;

import static org.bibsonomy.util.ValidationUtils.present;

public class InsertGroupMembershipLog extends LoggingInfoTrait {

    private final String username;
    private final Group group;

    public InsertGroupMembershipLog(User loggedUser, Date loggedTimestamp, String username, Group group) {
        super(loggedUser, loggedTimestamp);

        present(group);
        this.group = group;

        present(username);
        this.username = username;
    }

    public InsertGroupMembershipLog(User loggedUser, String username, Group group) {
        super(loggedUser);

        present(group);
        this.group = group;

        present(username);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Group getGroup() {
        return group;
    }
}
