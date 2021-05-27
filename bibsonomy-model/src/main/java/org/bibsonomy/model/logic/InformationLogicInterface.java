package org.bibsonomy.model.logic;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;

public interface InformationLogicInterface {

    /**
     * Returns details about a specified user
     *
     * In case of the requesting user is not logged in or he's not allowed to access <br>
     * the requested users data, a user containing only it's name is returned. <br>
     *
     * In case of the a non existing requested user or a deleted account, a complete empty user is returned.
     *
     * @param userName name of the user we want to get details from
     * @return details about a named user
     */
    User getUserDetails(String userName);

    /**
     * Returns details of one group.
     *
     * @param groupName
     * @param pending	<code>true</code> iff you want to get group details of
     * 					a pending group
     * @return the group's details, null else
     */
    public Group getGroupDetails(String groupName, boolean pending);

}
