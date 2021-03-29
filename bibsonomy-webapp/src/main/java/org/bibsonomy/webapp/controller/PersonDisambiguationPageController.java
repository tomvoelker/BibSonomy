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
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.person.PersonRoleRenderer;
import org.bibsonomy.webapp.command.DisambiguationPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.ExtendedRedirectViewWithAttributes;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * FIXME: move actions to separate controller
 *
 * this controller to disambiguate an author/editor of a publication
 * paths:
 *     - /person/INTERHASH/RELATIONROLE/PERSONNAME_INDEX
 *
 *     e.g.
 *     /person/182b65113dff41c2bd36e9444902dbe7a/author/2
 *
 * @author Christian Pfeiffer, Tom Hanika
 */
public class PersonDisambiguationPageController extends SingleResourceListController implements MinimalisticController<DisambiguationPageCommand>, ErrorAware {
	private PersonRoleRenderer personRoleRenderer;
	private URLGenerator urlGenerator;
	private Errors errors;

	@Override
	public DisambiguationPageCommand instantiateCommand() {
		final DisambiguationPageCommand command = new DisambiguationPageCommand();
		command.setPersonRoleRenderer(personRoleRenderer);
		return command;
	}

	@Override
	public View workOn(final DisambiguationPageCommand command) {
		final String requestedHash = command.getRequestedHash();
		if (!present(requestedHash)) {
			throw new MalformedURLSchemeException("error.disambiguation_without_hash");
		}

		// get the post that should be displayed
		final PostQueryBuilder postQueryBuilder = new PostQueryBuilder();
		postQueryBuilder.setGrouping(GroupingEntity.ALL)
				.setHash(requestedHash)
				.entriesStartingAt(1, 0);
		final List<Post<BibTex>> posts = this.logic.getPosts(postQueryBuilder.createPostQuery(BibTex.class));

		if (!present(posts)) {
			throw new ObjectNotFoundException(requestedHash);
		}

		// TODO: don't use the command to pass the post to the methods, please add a parameter for the post
		final Post<BibTex> post = posts.get(0);
		final String action = command.getRequestedAction();
		if ("newPerson".equals(action)) {
			return newAction(post, command);
		} else if ("linkPerson".equals(action)) {
			return linkAction(post, command);
		}

		if (!present(command.getRequestedIndex())) {
			throw new MalformedURLSchemeException("error.disambiguation.without_index");
		}

		return this.disambiguateAction(post, command);
	}

	private View disambiguateAction(final Post<BibTex> post, final DisambiguationPageCommand command) {
		command.setPost(post);

		final PersonResourceRelationType requestedRole = command.getRequestedRole();
		final int requestedIndex = command.getRequestedIndex().intValue();

		final BibTex publication = post.getResource();
		final List<ResourcePersonRelation> matchingRelations = this.logic.getResourceRelations(new ResourcePersonRelationQueryBuilder().byInterhash(publication.getInterHash()).byRelationType(requestedRole).byAuthorIndex(requestedIndex));

		/*
		 * redirect to the person page of the author/editor
		 */
		if (present(matchingRelations)) {
			return new ExtendedRedirectView(this.urlGenerator.getPersonUrl(matchingRelations.get(0).getPerson().getPersonId()));
		}

		final BibTex res = publication;
		final List<PersonName> personNames = PersonUtils.getPersonsByRoleWithFallback(res, requestedRole);

		if (!present(personNames) || requestedIndex < 0 || requestedIndex >= personNames.size()) {
			throw new ObjectNotFoundException(requestedRole + " for " + res.getInterHash());
		}

		final PersonName requestedName = personNames.get(requestedIndex);
		command.setPersonName(requestedName);

		// FIXME: move escape to es module
		/*
		 * get candidate persons with the name of the author/editor
		 */
		final String name = QueryParser.escape(BibTexUtils.cleanBibTex(requestedName.toString()));
		final PersonQuery personQuery = new PersonQuery(name);
		personQuery.setEnd(5);
		personQuery.setPhraseMatch(true);
		final List<Person> persons = this.logic.getPersons(personQuery);
		command.setPersonSuggestions(persons);

		/*
		 * get other publications of the author
		 */
		final PostQuery<GoldStandardPublication> query = new PostQuery<>(GoldStandardPublication.class);
		query.setPersonNames(Collections.singletonList(requestedName));
		final List<Post<GoldStandardPublication>> posts = this.logic.getPosts(query);
		command.setSuggestedPosts(posts);

		return Views.DISAMBIGUATION;
	}

	/**
	 * creates a new person, links te resource and redirects to the new person page
	 *
	 * @param post
	 * @param command
	 * @return
	 */
	private View newAction(final Post<BibTex> post, DisambiguationPageCommand command) {
		final Person person = createPersonEntity(post, command);
		try {
			linkToPerson(command, person, post);
		} catch (final ResourcePersonAlreadyAssignedException e) {
			return this.handleAlreadyAssignedRelations(post, command, e);
		}

		final ExtendedRedirectViewWithAttributes redirectView = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getPersonUrl(person.getPersonId()));
		redirectView.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "person.show.created.createAndLinkPerson");
		return redirectView;
	}

	private View handleAlreadyAssignedRelations(Post<BibTex> post, DisambiguationPageCommand command, ResourcePersonAlreadyAssignedException e) {
		final ResourcePersonRelation existingRelation = e.getExistingRelation();
		final Person existingPerson = existingRelation.getPerson();
		this.errors.reject("person.show.error.alreadyAssigned", new Object[]{
						PersonNameUtils.serializePersonName(e.getPubPersonName()),
						BibTexUtils.cleanBibTex(existingRelation.getPost().getResource().getTitle()),
						this.urlGenerator.getPersonUrl(existingPerson.getPersonId()),
						PersonNameUtils.serializePersonName(existingPerson.getMainName())
		}, "Person resource relation already assigned");

		return disambiguateAction(post, command);
	}

	private Person createPersonEntity(final Post<BibTex> post,final DisambiguationPageCommand command) {
		final Person person = new Person();

		final PersonName mainName = getMainPersonName(post, command);
		mainName.setMain(true);
		person.setMainName(mainName);

		this.logic.createPerson(person);
		return person;
	}

	private void linkToPerson(final DisambiguationPageCommand command, final Person person, final Post<BibTex> post) throws ResourcePersonAlreadyAssignedException {
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		resourcePersonRelation.setPerson(person);
		resourcePersonRelation.setPost(post);
		resourcePersonRelation.setRelationType(command.getRequestedRole());
		resourcePersonRelation.setPersonIndex(command.getRequestedIndex().intValue());
		this.logic.createResourceRelation(resourcePersonRelation);
	}

	private static PersonName getMainPersonName(final Post<BibTex> post, final DisambiguationPageCommand command) {
		final BibTex publication = post.getResource();
		final List<PersonName> personName = PersonUtils.getPersonsByRoleWithFallback(publication, command.getRequestedRole());
		final int personIndex = command.getRequestedIndex().intValue();

		if ((personName == null) || (personName.size() <= personIndex)) {
			throw new IllegalArgumentException("person not found");
		}

		return personName.get(personIndex);
	}

	/**
	 * links the resource and redirects to the person page
	 * @param command
	 * @return
	 */
	private View linkAction(final Post<BibTex> post, final DisambiguationPageCommand command) {
		final String personId = command.getRequestedPersonId();
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, personId);
		try {
			this.linkToPerson(command, person, post);
		} catch (final ResourcePersonAlreadyAssignedException e) {
			return this.handleAlreadyAssignedRelations(post, command, e);
		}

		final ExtendedRedirectViewWithAttributes redirectView = new ExtendedRedirectViewWithAttributes(this.urlGenerator.getPersonUrl(personId));
		redirectView.addAttribute(ExtendedRedirectViewWithAttributes.SUCCESS_MESSAGE_KEY, "person.show.created.linkPerson");
		return redirectView;
	}

	public void setPersonRoleRenderer(PersonRoleRenderer personRoleRenderer) {
		this.personRoleRenderer = personRoleRenderer;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}