package org.bibsonomy.sync;

/**
 * @author wla
 * @version $Id$
 */
public enum ConflictResolutionStrategy {
    CLIENT_WINS,
    SERVER_WINS,
    LAST_WINS,
    FIRST_WINS,
    ASK_USER;
}
