package org.bibsonomy.common.enums;

import java.util.Collections;
import java.util.Set;

import org.bibsonomy.util.Sets;

public enum ProjectRole {

    /** user */
    WORKER(2),

    /** moderator */
    MODERATOR(1, Sets.asSet(WORKER)),

    /** administrator */
    ADMINISTRATOR(0, Sets.asSet(MODERATOR, WORKER)),

    /** dummy */
    DUMMY(3),

    /** all non pending project roles */
    /* fixme */
    public static final Set<ProjectRole> PROJECT_ROLES = Sets.asSet(ProjectRole.ADMINISTRATOR, ProjectRole.MODERATOR, ProjectRole.WORKER);

    private final int role;
    private final java.util.Set<ProjectRole> impliedRoles;

    private ProjectRole(final int role) {
        this(role, Collections.<ProjectRole>emptySet());
    }

    private ProjectRole(final int role, final Set<ProjectRole> impliedRoles) {
        this.role = role;
        this.impliedRoles = impliedRoles;
    }

    /**
     * Returns the numerical representation of this object.
     *
     * @return The numerical representation of the object.
     */
    public int getRole() {
        return this.role;
    }

    /**
     * Creates an instance of this class by its String representation.
     *
     * @param level
     *        - a String representing the object. Must be an integer number.
     * @return The corresponding object.
     */
    public static ProjectRole getProjectRole(final String level) {
        if (level == null) {
            return WORKER;
        }
        return getProjectRole(Integer.parseInt(level));
    }

    /**
     * Creates an instance of this class by its Integer representation.
     *
     * @param level
     *        - an Integer representing the object.
     * @return The corresponding object.
     */
    public static ProjectRole getProjectRole(final int level) {
        for (final ProjectRole r : ProjectRole.values()) {
            if (r.role == level) {
                return r;
            }
        }
        throw new IllegalArgumentException("unknown project role id " + level);
    }

    /**
     * @param requiredRole
     * @return <code>true</code> if the required role equals the actual role or
     * the required role is implied by this role
     */
    public boolean hasRole(final ProjectRole requiredRole) {
        return this.equals(requiredRole) || this.impliedRoles.contains(requiredRole);
    }
}
