/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class GetUserListOfGroupStrategy extends AbstractGetListStrategy<List<User>> {

	private final String groupName;

	/**
	 * Constructor
	 * 
	 * @param context - the context of the request
	 * @param groupName - the group name
	 */
	public GetUserListOfGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
	}

	@Override
	public String getContentType() {
		return "users";
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForGroupMembers(this.groupName);
	}

	@Override
	protected List<User> getList() {
		return this.getLogic().getUsers(null, GroupingEntity.GROUP, this.groupName, null, null, null, null, null, getView().getStartValue(), getView().getEndValue());
	}

	@Override
	protected void render(final Writer writer, final List<User> resultList) {
		this.getRenderer().serializeUsers(writer, resultList, getView());
	}
}