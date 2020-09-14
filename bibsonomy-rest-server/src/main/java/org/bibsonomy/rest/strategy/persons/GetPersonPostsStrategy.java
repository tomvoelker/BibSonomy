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

package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.*;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

/**
 * Strategy to get the publications of a person by their ID.
 *
 * @author kchoong
 */
public class GetPersonPostsStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {

	private final String personId;
	private final List<String> tags;
	private final String search;

	/**
	 * @param context
	 * @param personId
	 */
	public GetPersonPostsStrategy(Context context, String personId) {
		super(context);
		this.personId = personId;
		this.tags = context.getTags(RESTConfig.TAGS_PARAM);
		this.search = context.getStringAttribute(RESTConfig.SEARCH_PARAM, null);
	}

	@Override
	protected void render(final Writer writer, final List<? extends Post<? extends Resource>> resultList) {
		this.getRenderer().serializePosts(writer, resultList, this.getView());
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		final Person person = this.getLogic().getPersonById(PersonIdType.PERSON_ID, personId);
		if (person != null) {
			// check, if a user has claimed this person and configured their person settings
			final String linkedUser = person.getUser();
			if (linkedUser != null) {
				// Check, if the user set their person posts to gold standards or 'myown'-tagged posts
				// we use the admin logic here, because the setting might not be visible to the current logged in user

				// FIXME: because we need this also in the webapp, this should be moved to the logic and not handled by
				// the rest server or the webapp controller
				final User user = this.getAdminLogic().getUserDetails(linkedUser);
				int personPostsStyleSettings = user.getSettings().getPersonPostsStyle();

				// TODO: replace check after using an enum for this setting
				if (personPostsStyleSettings > 0) {
					// Get 'myown' posts of the linked user

					// TODO: use the myown system tag
					this.tags.add("myown");
					final PostQueryBuilder myOwnqueryBuilder = new PostQueryBuilder()
							.setStart(this.getView().getStartValue())
							.setEnd(this.getView().getEndValue())
							.setTags(this.tags)
							.setGrouping(GroupingEntity.USER)
							.setGroupingName(linkedUser);
					return this.getLogic().getPosts(myOwnqueryBuilder.createPostQuery(BibTex.class));
				}

				// Default: gold standards
				final PostQueryBuilder queryBuilder = new PostQueryBuilder()
						.setStart(this.getView().getStartValue())
						.setEnd(this.getView().getEndValue())
						.setTags(this.tags)
						.setSearch(this.search);
				queryBuilder.setGrouping(GroupingEntity.PERSON)
						.setGroupingName(this.personId);
				return this.getLogic().getPosts(queryBuilder.createPostQuery(GoldStandardPublication.class));
			}
		}
		return new LinkedList<>();
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPersonPosts(this.personId);
	}
}
