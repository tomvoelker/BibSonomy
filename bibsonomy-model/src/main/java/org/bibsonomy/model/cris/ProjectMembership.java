package org.bibsonomy.model.cris;

import org.bibsonomy.common.enums.ProjectRole;
import org.bibsonomy.model.User;

public class ProjectMembership {

    private User user;
    private ProjectRole projectRole;

    /**
     * Default Constructor
     */
    public ProjectMembership() {
        // noop
    }

    /**
     *
     * @param user
     * @param projectRole
     */
    public ProjectMembership(User user, ProjectRole projectRole) {
        this.user = user;
        this.projectRole = projectRole;
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
     * @return the projectRole
     */
    public ProjectRole getProjectRole() {
        return this.projectRole;
    }

    /**
     * @param projectRole the projectRole to set
     */
    public void setProjectRole(ProjectRole projectRole) {
        this.projectRole = projectRole;
    }

    /**
     * toString. User projectRole
     * @return string
     */
    @Override
    public String toString() {
        return this.user + " " + this.projectRole;
    }
}
