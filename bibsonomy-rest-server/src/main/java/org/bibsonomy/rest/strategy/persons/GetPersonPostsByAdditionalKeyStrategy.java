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
import org.bibsonomy.model.*;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy to get the publications of a person by an additional key.
 *
 * @author kchoong
 */
public class GetPersonPostsByAdditionalKeyStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {

	private final String keyName;
	private final String keyValue;
	private final List<String> tags;
	private final String search;

	/**
	 * @param context
	 * @param keyName
	 * @param keyValue
	 */
	public GetPersonPostsByAdditionalKeyStrategy(Context context, String keyName, String keyValue) {
		super(context);
		this.keyName = keyName;
		this.keyValue = keyValue;
		this.tags = context.getTags(RESTConfig.TAGS_PARAM);
		this.search = context.getStringAttribute(RESTConfig.SEARCH_PARAM, null);
	}

	@Override
	protected void render(final Writer writer, final List<? extends Post<? extends Resource>> resultList) {
		this.getRenderer().serializePosts(writer, resultList, this.getView());
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {

		PostQueryBuilder queryBuilder = new PostQueryBuilder()
				.start(this.getView().getStartValue())
				.end(this.getView().getEndValue())
				.setTags(this.tags)
				.search(this.search);
		Person person = this.getLogic().getPersonByAdditionalKey(this.keyName, this.keyValue);
		// Check, if a user has claimed this person and configured their person settings
		if (person != null && person.getUser() != null) {
			// Check, if the user set their person posts to gold standards or 'myown'-tagged posts
			User user = this.getLogic().getUserDetails(person.getUser());
			PersonPostsStyle personPostsStyleSettings = user.getSettings().getPersonPostsStyle();
			if (personPostsStyleSettings == PersonPostsStyle.MYOWN) {
				// 'myown'-tagged posts
				// TODO use system tag
				this.tags.add("myown");
				queryBuilder.setGrouping(GroupingEntity.USER)
						.setGroupingName(person.getUser())
						.setTags(this.tags);
				return this.getLogic().getPosts(queryBuilder.createPostQuery(BibTex.class));
			}
			// Default: gold standards
			queryBuilder.setGrouping(GroupingEntity.PERSON)
					.setGroupingName(person.getPersonId());
			return this.getLogic().getPosts(queryBuilder.createPostQuery(GoldStandardPublication.class));
		}
		return new ArrayList<>();
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPersonPostsByAdditionalKey(this.keyName, this.keyValue);
	}
}
