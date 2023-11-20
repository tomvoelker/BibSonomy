/**
 * BibSonomy-Rest-Server - The REST-server.
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
package org.bibsonomy.rest.strategy.clipboard;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.users.GetUserPostsStrategy;

/**
 * @author wla
 */
public class GetClipboardStrategy extends GetUserPostsStrategy {

	/**
	 * 
	 * @param context
	 * @param userName 
	 */
	public GetClipboardStrategy(final Context context, final String userName) {
		super(context, userName);
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.CLIPBOARD)
				.setGroupingName(this.userName)
				.setScope(QueryScope.LOCAL)
				.start(this.getView().getStartValue())
				.end(this.getView().getEndValue());
		return this.getLogic().getPosts(postQueryBuilder.createPostQuery(BibTex.class));
	}

}
