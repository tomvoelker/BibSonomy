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
package org.bibsonomy.rest.strategy.persons;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonPostsStyle;
import org.bibsonomy.model.enums.PersonResourceRelationSortKey;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.querybuilder.PersonQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.util.PersonResourceRelationUtils;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;


/**
 * FIXME: move the duplicate code to the code (see webapp)
 *
 * Strategy to get the publications related to a person.
 *
 * If the linked user of the person has set their publication option as 'myown' posts, we will return those,
 * otherwise we convert resource-person-relations to goldstandard publication posts.
 *
 * @author kchoong
 */
public class GetPersonPostsStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {

	private static final Log log = LogFactory.getLog(GetPersonPostsStrategy.class);

	private String personId;
	private final AdditionalKey additionalKey;
	private final Date beforeChangeDate;
	private final Date afterChangeDate;
	private final boolean withPersons;
	private final boolean withPosts;
	private final boolean withPersonsOfPosts;
	private final boolean onlyTheses;
	private final boolean groupByInterhash;

	/**
	 * constructor for /persons/posts
	 * @param context
	 */
	public GetPersonPostsStrategy(final Context context) {
		super(context);
		this.personId = context.getStringAttribute(RESTConfig.PERSON_ID_PARAM, null);
		this.additionalKey = RESTUtils.getAdditionalKeyParam(context);
		this.beforeChangeDate = RESTUtils.getDateParam(context, RESTConfig.BEFORE_CHANGE_DATE_PARAM);
		this.afterChangeDate = RESTUtils.getDateParam(context, RESTConfig.AFTER_CHANGE_DATE_PARAM);
		this.withPersons = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.WITH_PERSONS_PARAM, "false"));
		this.withPosts = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.WITH_POSTS_PARAM, "false"));
		this.withPersonsOfPosts = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.WITH_PERSONS_OF_POSTS_PARAM, "false"));
		this.onlyTheses = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.ONLY_THESES_PARAM, "false"));
		this.groupByInterhash = Boolean.parseBoolean(context.getStringAttribute(RESTConfig.GROUP_BY_INTERHASH_PARAM, "false"));
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		final List<Post<? extends BibTex>> results = new LinkedList<>();
		final Person person = this.getPerson();

		if (!present(person) && !present(this.beforeChangeDate) && !present(this.afterChangeDate)) {
			log.debug("No change date given and no person was found with " + this.personId + " : " + this.additionalKey);
			return results;
		}

		if (present(person)) {
			// Set person id, if additional keys were used
			this.personId = person.getPersonId();

			// Check, if a user has claimed this person and opt for myown-posts
			if (present(person.getUser())) {
				// Get person posts style settings of the linked user
				final User user = this.getAdminLogic().getUserDetails(person.getUser());
				final PersonPostsStyle personPostsStyle = user.getSettings().getPersonPostsStyle();
				if (personPostsStyle == PersonPostsStyle.MYOWN) {
					return this.handleMyOwnPosts(person);
				}
			}
		}

		// Default: return the gold standards
		final ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
				.byPersonId(this.personId)
				.beforeChangeDate(this.beforeChangeDate)
				.afterChangeDate(this.afterChangeDate)
				.withPersons(this.withPersons)
				.withPosts(this.withPosts)
				.withPersonsOfPosts(this.withPersonsOfPosts)
				.onlyTheses(this.onlyTheses)
				.groupByInterhash(this.groupByInterhash)
				.sortBy(PersonResourceRelationSortKey.PublicationYear)
				.orderBy(SortOrder.DESC)
				.fromTo(this.getView().getStartValue(), this.getView().getEndValue());
		final List<ResourcePersonRelation> resourceRelations = this.getLogic().getResourceRelations(queryBuilder.build());

		for (final ResourcePersonRelation resourcePersonRelation : resourceRelations) {
			final Post<? extends BibTex> post = resourcePersonRelation.getPost();
			final BibTex publication = post.getResource();
			// we explicitly do not want ratings on the person pages because this might cause users of the genealogy feature to hesitate putting in their dissertations
			publication.setRating(null);
			publication.setNumberOfRatings(null);

			// only add author and editor relations to the result list
			if (PersonResourceRelationUtils.isAuthorEditorRelation(resourcePersonRelation)) {
				results.add(resourcePersonRelation.getPost());
			}
		}

		return results;
	}

	protected List<? extends Post<? extends Resource>> handleMyOwnPosts(final Person person) {
		// Get 'myown' posts of the linked user
		final PostQueryBuilder myOwnqueryBuilder = new PostQueryBuilder()
				.setGrouping(GroupingEntity.USER)
				.setGroupingName(person.getUser())
				.setTags(Collections.singletonList("myown")) // TODO: use the myown system tag
				.fromTo(this.getView().getStartValue(), this.getView().getEndValue());

		return this.getLogic().getPosts(myOwnqueryBuilder.createPostQuery(BibTex.class));
	}

	private Person getPerson() {
		if (present(this.personId)) {
			return this.getLogic().getPersonById(PersonIdType.PERSON_ID, this.personId);
		}

		if (present(this.additionalKey)) {
			PersonQueryBuilder queryBuilder = new PersonQueryBuilder().byAdditionalKey(this.additionalKey);
			List<Person> persons = this.getLogic().getPersons(queryBuilder.build());
			if (present(persons)) {
				return persons.get(0);
			}
		}

		return null;
	}

	@Override
	protected void render(final Writer writer, final List<? extends Post<? extends Resource>> resultList) {
		this.getRenderer().serializePosts(writer, resultList, this.getView());
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		if (present(this.additionalKey)) {
			return this.getUrlRenderer().createUrlBuilderForPersonPostsByAdditionalKey(this.additionalKey);
		}
		return this.getUrlRenderer().createUrlBuilderForPersonPosts(this.personId);
	}

}
