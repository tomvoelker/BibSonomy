/**
 * BibSonomy-Rest-Server - The REST-server.
 * <p>
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * http://www.kde.cs.uni-kassel.de/
 * Data Mining and Information Retrieval Group,
 * University of Würzburg, Germany
 * http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * http://www.l3s.de/
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

import java.io.Writer;
import java.util.List;

/**
 * Strategy to get the gold standard publication of a person by their ID.
 *
 * @author kchoong
 */
public class GetPersonPostsStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {

	private final String personId;
	private final List<String> tags;
	private final String tagString;
	private final String search;
	private final Class<? extends Resource> resourceType;

	/**
	 * @param context
	 * @param personId
	 */
	public GetPersonPostsStrategy(Context context, String personId) {
		super(context);
		this.personId = personId;
		this.tags = context.getTags(RESTConfig.TAGS_PARAM);
		this.tagString = context.getStringAttribute(RESTConfig.TAGS_PARAM, null);
		this.search = context.getStringAttribute(RESTConfig.SEARCH_PARAM, null);
		this.resourceType = GoldStandardPublication.class;
	}

	@Override
	protected void render(final Writer writer, final List<? extends Post<? extends Resource>> resultList) {
		this.getRenderer().serializePosts(writer, resultList, this.getView());
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		PostQuery<GoldStandardPublication> query = new PostQueryBuilder()
						.setGrouping(GroupingEntity.PERSON)
						.setGroupingName(this.personId)
						.setStart(this.getView().getStartValue())
						.setEnd(this.getView().getEndValue())
						.createPostQuery(GoldStandardPublication.class);
		return this.getLogic().getPosts(query);
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPersonPosts(this.personId);
	}
}
