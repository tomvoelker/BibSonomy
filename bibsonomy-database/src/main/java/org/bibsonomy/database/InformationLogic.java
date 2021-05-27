/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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

package org.bibsonomy.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.UserDatabaseManager;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.InformationLogicInterface;
import org.bibsonomy.model.util.GroupUtils;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * This is a minimal database implementation to retrieve certain informations
 * such as user/group settings without using an admin logic.
 *
 * @author kchoong
 */
public class InformationLogic implements InformationLogicInterface {

    private static final Log log = LogFactory.getLog(InformationLogic.class);

    private User loginUser;
    private DBSessionFactory dbSessionFactory;

    private final UserDatabaseManager userDBManager;
    private final GroupDatabaseManager groupDBManager;
    private final PersonDatabaseManager personDBManager;

    protected InformationLogic() {
        this.userDBManager = UserDatabaseManager.getInstance();
        this.groupDBManager = GroupDatabaseManager.getInstance();
        this.personDBManager = PersonDatabaseManager.getInstance();
    }

    /**
     * Only get the details and linked person of the given user.
     *
     * @param userName name of the user we want to get details from
     * @return
     */
    @Override
    public User getUserDetails(String userName) {
        try (final DBSession session = this.openSession()) {
            /*
             * We don't use userName but user.getName() in the remaining part of
             * this method, since the name gets normalized in getUserDetails().
             */
            final User user = this.userDBManager.getUserDetails(userName, session);

            /*
             * get the claimed person for the user only if not a dummy user was requested
             */
            final String foundUserName = user.getName();
            if (present(foundUserName)) {
                final Person claimedPerson = this.personDBManager.getPersonByUser(foundUserName, session);
                user.setClaimedPerson(claimedPerson);
            }

            /*
             * return a complete empty user, in case of a deleted user
             */
            if (user.getRole() == Role.DELETED) {
                return new User();
            }

            return user;
        }
    }

    /**
     * Only get the details of the given group name
     *
     * @param groupName name of the group we want details from
     * @param pending	pending groups are not supported in this logic
     * @return
     */
    @Override
    public Group getGroupDetails(String groupName, boolean pending) {
        try (final DBSession session = this.openSession()) {

            final Group myGroup = this.groupDBManager.getGroup(this.loginUser.getName(), groupName, true, true, session);
            if (!GroupUtils.isValidGroup(myGroup)) {
                return null;
            }

            return myGroup;
        }
    }

    /**
     * Returns a new database session. If a user is logged in, he gets the
     * master connection, if not logged in, the secondary connection
     */
    private DBSession openSession() {
        return this.dbSessionFactory.getDatabaseSession();
    }

    /**
     * @param dbSessionFactory the dbSessionFactory to set
     */
    public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
        this.dbSessionFactory = dbSessionFactory;
    }

    /**
     * @param userName set the username of the admin user
     */
    public void setUserName(String userName) {
        // create admin user to have access admin-only information such as settings of users
        this.loginUser = new User(userName);
        this.loginUser.setRole(Role.ADMIN);
    }
}
