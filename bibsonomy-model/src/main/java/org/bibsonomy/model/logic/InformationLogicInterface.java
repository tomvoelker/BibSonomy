/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
