package org.bibsonomy.model.cris;

import org.bibsonomy.common.enums.GroupRole;
import org.bibsonomy.model.User;

public class ProjectMembership {

    private User user;
    private GroupRole groupRole;

    /**
     * Default Constructor
     */
    public ProjectMembership() {
        // noop
    }

    /**
     *
     * @param user
     * @param groupRole
     */
    public ProjectMembership(User user, GroupRole groupRole) {
        this.user = user;
        this.groupRole = groupRole;
     }

    /**
     * @return the user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the groupRole
     */
    public GroupRole getGroupRole() {
        return this.groupRole;
    }

    /**
     * @param groupRole the groupRole to set
     */
    public void setGroupRole(GroupRole groupRole) {
        this.groupRole = groupRole;
    }

    /**
     * toString. User GroupRole
     * @return string
     */
    @Override
    public String toString() {
        return this.user + " " + this.groupRole;
    }
}
