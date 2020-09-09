/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.ajax;


import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPublicationCommand;
import org.bibsonomy.webapp.controller.PersonPageController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * TODO: document controller
 *
 * FIXME: remove copy paste code
 *
 * œauthor mho
 *
 */
public class PersonPublicationAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPublicationCommand> {

	@Override
	public AjaxPersonPublicationCommand instantiateCommand() {
		return new AjaxPersonPublicationCommand();
	}

	@Override
	public View workOn(final AjaxPersonPublicationCommand command) {
		final RequestWrapperContext context = command.getContext();

		/*
		 * check ckey
		 */
		//if (!command.getContext().isValidCkey()) {
			//command.setResponseString(getXmlError("error.field.valid.ckey", null, command.getFileID(), null, locale));
			// TODO ERROR MESSAGE
		//	command.setResponseString("ERROR");
		//	return Views.AJAX_ERRORS;
		//}

		final String requestedPersonId = command.getRequestedPersonId();
		/*
		 * get the person; if person with the requested id was merged with another person, this method
		 * throws a ObjectMovedException and the wrapper will render the redirect
		 */
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, requestedPersonId);
		if (!present(person) || !present(command.getPage())) {
			return Views.AJAX_ERRORS;
		}

		command.setPersonPostsStyleSettings(0);
		int postsPerPage = 20;

		final User authenticatedUser = this.logic.getAuthenticatedUser();

		if (authenticatedUser != null && authenticatedUser.getSettings().getListItemcount() > 0) {
			postsPerPage = authenticatedUser.getSettings().getListItemcount();
		}

		int start = command.getPage() * postsPerPage;
		int end = start + postsPerPage;

		// Get the linked user's person posts style settings
		String linkedUser = person.getUser();
		if (present(linkedUser)) {
			User user = this.logic.getUserDetails(linkedUser);
			command.setPersonPostsStyleSettings(user.getSettings().getPersonPostsStyle());

			// Get 'myown' posts of the linked user
			PostQueryBuilder myOwnqueryBuilder = new PostQueryBuilder()
					.setStart(start)
					.setEnd(end)
					.setTags(new ArrayList<>(Collections.singletonList("myown")))
					.setGrouping(GroupingEntity.USER)
					.setGroupingName(linkedUser);
			final List<Post<BibTex>> myownPosts = this.logic.getPosts(myOwnqueryBuilder.createPostQuery(BibTex.class));


			command.setMyownPosts(myownPosts);
		}

		// TODO: this needs to be removed/refactored as soon as the ResourcePersonRelationQuery.ResourcePersonRelationQueryBuilder accepts start/end
		ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
				.byPersonId(person.getPersonId())
				.withPosts(true)
				.withPersonsOfPosts(true)
				.groupByInterhash(true)
				.orderBy(PersonResourceRelationOrder.PublicationYear)
				.fromTo(start, end);
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


		// TODO: maybe this should be done in the view?
		final List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations(query);
		final List<ResourcePersonRelation> otherAuthorRelations = new ArrayList<>();

		for (final ResourcePersonRelation resourcePersonRelation : resourceRelations) {
			final Post<? extends BibTex> post = resourcePersonRelation.getPost();
			final BibTex publication = post.getResource();
			final boolean isThesis = publication.getEntrytype().toLowerCase().endsWith("thesis");
			final boolean isAuthorEditorRelation = PersonPageController.PUBLICATION_RELATED_RELATION_TYPES.contains(resourcePersonRelation.getRelationType());

			if (isAuthorEditorRelation) {
				if (!isThesis) {
					otherAuthorRelations.add(resourcePersonRelation);
				}
			}

			// we explicitly do not want ratings on the person pages because this might cause users of the genealogy feature to hesitate putting in their dissertations
			publication.setRating(null);
			publication.setNumberOfRatings(null);
		}


		command.setOtherPubs(otherAuthorRelations);

		return Views.AJAX_PERSON_PUBLICATIONS;
	}

}
