/**
 * BibSonomy-Database - Database for BibSonomy.
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

package org.bibsonomy.database.managers.chain.group.get;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GroupDatabaseManager;
import org.bibsonomy.database.managers.chain.group.GroupChainElement;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;

import java.util.List;


/**
 * Handles the retrieval of all organization groups.
 *
 * @author kchoong
 */
public class GetOrganizationGroups extends GroupChainElement {

	public GetOrganizationGroups(GroupDatabaseManager groupDatabaseManager) {
		super(groupDatabaseManager);
	}

	@Override
	protected List<Group> handle(final QueryAdapter<GroupQuery> queryAdapter, final DBSession session) {
		final GroupQuery param = queryAdapter.getQuery();
		return this.groupDb.getAllOrganizationGroups(param.getStart(), param.getEnd(), session);
	}

	@Override
	protected boolean canHandle(final QueryAdapter<GroupQuery> param) {
		final Boolean organization = param.getQuery().getOrganization();
		return present(organization) && organization.booleanValue();
	}
}
