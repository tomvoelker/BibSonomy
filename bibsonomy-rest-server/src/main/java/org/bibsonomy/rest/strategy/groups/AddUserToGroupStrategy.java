/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
import java.io.Reader;
import java.util.List;
import org.bibsonomy.common.enums.GroupRole;

import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
@Deprecated
public class AddUserToGroupStrategy extends Strategy {
	private final Reader doc;
	private final String groupName;
	
	/**
	 * @param context
	 * @param groupName
	 */
	public AddUserToGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
		this.doc = context.getDocument();
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException {
		/*
		 * parse users
		 */
		final List<User> users = this.getRenderer().parseUserList(this.doc);
		/*
		 * create empty group with desired name
		 */
		final Group group = new Group(this.groupName);
		/*
		 * add users to group
		 */
		try {
			// TODO: Convert this to the new group concept, aka you can't just
			// add users.
			throw new UnsupportedOperationException("not yet implemented");
//			for (User u : users)
//				this.getLogic().updateGroup(this.groupName, GroupUpdateOperation.ADD_NEW_USER, new GroupMembership(u, GroupRole.USER, false));
		}
		catch (ValidationException ve) {
			throw new BadRequestOrResponseException(ve.getMessage());
		}
		/*
		 * no exception -> assume success
		 */
//		this.getRenderer().serializeGroupId(this.writer, this.groupName); // serializeOK(this.writer);
	}
}