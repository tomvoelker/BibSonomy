/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.groups;

import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class RemoveUserFromGroupStrategy extends Strategy {
	private final String groupName;
	private final GroupMembership membership;

	/**
	 * @param context
	 * @param groupName
	 * @param userName
	 */
	public RemoveUserFromGroupStrategy(final Context context, final String groupName, final String userName) {
		super(context);
		this.groupName = groupName;
		this.membership = new GroupMembership();
		this.membership.setUser(new User(userName));
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		this.getLogic().updateGroup(new Group(this.groupName), GroupUpdateOperation.REMOVE_MEMBER, this.membership);
		// no exception -> assume success
		this.getRenderer().serializeOK(writer);
	}
}