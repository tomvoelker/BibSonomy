/**
 * BibSonomy-Rest-Client - The REST-client.
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
package org.bibsonomy.rest.client.queries.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * Use this Class to receive an ordered list of all posts.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
@Getter
@Setter
public final class GetPostsQuery extends AbstractQuery<List<Post<? extends Resource>>> {
	private static final Log log = LogFactory.getLog(GetPostsQuery.class);

	private final String userName;
	private final Class<? extends Resource> resourceType;
	private final GroupingEntity grouping;
	private final String groupingValue;
	private final String search;
	private final List<String> tags;
	private final String resourceHash;
	private final List<SortCriteria> sortCriteria;
	private final QueryScope searchType;

	private final int start;
	private final int end;

	/**
	 * Gets bibsonomy's posts list.
	 */
	public GetPostsQuery(PostQuery<? extends Resource> query, User loggedInUser) {
		this.grouping = present(query.getGrouping()) ? query.getGrouping() : GroupingEntity.ALL;
		this.groupingValue = query.getGroupingName();
		if (this.grouping != GroupingEntity.ALL && !present(this.groupingValue)) {
			throw new IllegalArgumentException("no grouping value given");
		}

		this.resourceHash = query.getHash();
		this.resourceType = query.getResourceClass();
		this.tags = query.getTags();
		this.search = query.getSearch();
		this.sortCriteria = query.getSortCriteria();
		this.userName = loggedInUser.getName();
		this.searchType = query.getScope();

		this.start = query.getStart();
		this.end = query.getEnd();
	}

	@Override
	public List<Post<? extends Resource>> getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		try {
			return this.getRenderer().parsePostList(this.downloadedDocument, NoDataAccessor.getInstance());
		} catch (final InternServerException ex) {
			throw new BadRequestOrResponseException(ex);
		}
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		if (GroupingEntity.CLIPBOARD.equals(this.grouping)) {
			final String clipboardUrl = this.getUrlRenderer().createHrefForClipboard(this.userName, null);
			this.downloadedDocument = performGetRequest(clipboardUrl);
			return;
		}
		
		final String url = this.getUrlRenderer().createHrefForPosts(this.grouping, this.groupingValue, this.resourceType, this.tags, this.resourceHash, this.search, this.sortCriteria, this.start, this.end, this.searchType);
		if (log.isDebugEnabled()) {
			log.debug("GetPostsQuery doExecute() called - URL: " + url);
		}
		this.downloadedDocument = performGetRequest(url);
	}
}