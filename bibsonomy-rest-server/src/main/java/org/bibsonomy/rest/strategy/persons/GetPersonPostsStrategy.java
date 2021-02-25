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
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.Sets;
import org.bibsonomy.util.UrlBuilder;

import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Strategy to get the publications of a person by their ID.
 *
 * @author kchoong
 */
public class GetPersonPostsStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {

	private final String personId;
	private final List<String> tags;
	private final String search;

	// TODO REMOVE ASAP
	public static final Set<PersonResourceRelationType> PUBLICATION_RELATED_RELATION_TYPES = Sets.asSet(PersonResourceRelationType.AUTHOR, PersonResourceRelationType.EDITOR);


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
//				final PostQueryBuilder queryBuilder = new PostQueryBuilder()
//						.setStart(this.getView().getStartValue())
//						.setEnd(this.getView().getEndValue())
//						.setTags(this.tags)
//						.setSearch(this.search);
//				queryBuilder.setGrouping(GroupingEntity.PERSON)
//						.setGroupingName(this.personId);

////////////////////////////////////////////////////////////////////
/////////////	REFACTOR 	////////////////////////////////
////////////////////////////////////////////////////////////////////
				// TODO: this needs to be removed/refactored as soon as the ResourcePersonRelationQuery.ResourcePersonRelationQueryBuilder accepts start/end
				ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
						.byPersonId(person.getPersonId())
						.withPosts(true)
						.withPersonsOfPosts(true)
						.groupByInterhash(true)
						.orderBy(PersonResourceRelationOrder.PublicationYear)
						.fromTo(this.getView().getStartValue(), this.getView().getEndValue());

				ResourcePersonRelationQuery.ResourcePersonRelationQueryBuilder builder = new ResourcePersonRelationQuery.ResourcePersonRelationQueryBuilder();

				builder.setAuthorIndex(queryBuilder.getAuthorIndex())
						.setEnd(queryBuilder.getEnd())
						.setGroupByInterhash(queryBuilder.isGroupByInterhash())
						.setInterhash(queryBuilder.getInterhash())
						.setOrder(queryBuilder.getOrder())
						.setPersonId(queryBuilder.getPersonId())
						.setRelationType(queryBuilder.getRelationType())
						.setStart(queryBuilder.getStart())
						.setWithPersons(queryBuilder.isWithPersons())
						.setWithPersonsOfPosts(queryBuilder.isWithPersonsOfPosts())
						.setWithPosts(queryBuilder.isWithPosts());

				ResourcePersonRelationQuery query = builder.build();

				final List<ResourcePersonRelation> resourceRelations = this.getLogic().getResourceRelations(query);
				final List<Post<? extends BibTex>> otherAuthorRelations = new ArrayList<>(); // !!


				for (final ResourcePersonRelation resourcePersonRelation : resourceRelations) {
					final Post<? extends BibTex> post = resourcePersonRelation.getPost();
					final BibTex publication = post.getResource();
					final boolean isThesis = publication.getEntrytype().toLowerCase().endsWith("thesis");
					final boolean isAuthorEditorRelation = PUBLICATION_RELATED_RELATION_TYPES.contains(resourcePersonRelation.getRelationType());

					if (isAuthorEditorRelation) {
						if (!isThesis) {
							otherAuthorRelations.add(resourcePersonRelation.getPost());
						}
					}

					// we explicitly do not want ratings on the person pages because this might cause users of the genealogy feature to hesitate putting in their dissertations
					publication.setRating(null);
					publication.setNumberOfRatings(null);
				}

				return otherAuthorRelations;
////////////////////////////////////////////////////////////////////
/////////////	REFACTOR 	////////////////////////////////
////////////////////////////////////////////////////////////////////

				//return this.getLogic().getPosts(queryBuilder.createPostQuery(GoldStandardPublication.class));
			}
		}
		return new LinkedList<>();
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForPersonPosts(this.personId);
	}
}
