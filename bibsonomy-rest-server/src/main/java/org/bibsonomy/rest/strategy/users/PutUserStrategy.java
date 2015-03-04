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
package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * strategy for updating an user
 * 		- users/USERNAME (HTTP-Method: PUT)
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class PutUserStrategy extends AbstractUpdateStrategy {
	private final String userName;

	/**
	 * @param context
	 * @param userName
	 */
	public PutUserStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
	}

	@Override
	protected void render(final Writer writer, final String userID) {
		this.getRenderer().serializeUserId(writer, userID);
	}

	@Override
	protected String update() throws InternServerException {
		final User user = this.getRenderer().parseUser(this.doc);
		// ensure to use the right user name
		user.setName(this.userName);
		/*
		 * FIXME: better heuristic! e.g., ensure that
		 * - calling user is admin (?)
		 */
		final UserUpdateOperation userUpdateOperation;
		if ((user.getPrediction() != null) || (user.getSpammer() != null)) {
			userUpdateOperation = UserUpdateOperation.UPDATE_SPAMMER_STATUS;
		} else {
			userUpdateOperation = UserUpdateOperation.UPDATE_ALL;
		}
		return this.getLogic().updateUser(user, userUpdateOperation);
	}
}