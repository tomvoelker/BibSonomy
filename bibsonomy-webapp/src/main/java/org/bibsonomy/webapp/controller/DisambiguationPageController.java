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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.exception.LogicException;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.person.PersonRoleRenderer;
import org.bibsonomy.webapp.command.DisambiguationPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Pfeiffer, Tom Hanika
 */
public class DisambiguationPageController extends SingleResourceListController implements MinimalisticController<DisambiguationPageCommand> {
	/**
	 * put into the session to tell the personPageController that the person has just been created
	 */
	public static final String ACTION_KEY_CREATE_AND_LINK_PERSON = "createAndLinkPerson";
	public static final String ACTION_KEY_LINK_PERSON = "linkPerson";
	
	private RequestLogic requestLogic;
	private PersonRoleRenderer personRoleRenderer;
	
	@Override
	public DisambiguationPageCommand instantiateCommand() {
		final DisambiguationPageCommand command = new DisambiguationPageCommand();
		command.setPersonRoleRenderer(personRoleRenderer);
		return command;
	}
	
	@Override
	public View workOn(final DisambiguationPageCommand command) {
		if (command.getRequestedHash() == null) {
			throw new ObjectNotFoundException(command.getRequestedHash());
		}
		
		final List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, command.getRequestedHash(), null, null, null, null, null, null, 0, 100);
		if (!present(posts)) {
			throw new ObjectNotFoundException(command.getRequestedHash());
		}
		command.setPost(posts.get(0));
		if ("newPerson".equals(command.getRequestedAction())) {
			return newAction(command);
		} else if ("linkPerson".equals(command.getRequestedAction())) {
			return linkAction(command);
		}
		
		if (!present(command.getRequestedIndex())) {
			throw new MalformedURLSchemeException("error.disambiguation.without_index");
		}
		
		return disambiguateAction(command);
	}

	private View disambiguateAction(final DisambiguationPageCommand command) {
		final PersonResourceRelationType requestedRole = command.getRequestedRole();
		final int requestedIndex = command.getRequestedIndex().intValue();
		
		final List<ResourcePersonRelation> matchingRelations = this.logic.getResourceRelations().byInterhash(command.getPost().getResource().getInterHash()).byRelationType(requestedRole).byAuthorIndex(requestedIndex).getIt();
		if (matchingRelations.size() > 0 ) {
			// FIXME: cache urlgenerator
			return new ExtendedRedirectView(new URLGenerator().getPersonUrl(matchingRelations.get(0).getPerson().getPersonId()));
		}
		
		final BibTex res = command.getPost().getResource();
		List<PersonName> persons = res.getPersonNamesByRole(requestedRole);
		// MacGyver-fix, in case there are multiple similar simhash1 caused by author == editor  
		if (persons == null ){
			persons = getPersonsByFallBack(res, requestedRole);
		}
		
		if (!present(persons) || requestedIndex < 0 || requestedIndex >= persons.size()) {
			throw new ObjectNotFoundException(requestedRole + " for " + res.getInterHash());
		}
		
		final PersonName requestedName = persons.get(requestedIndex);
		command.setPersonName(requestedName);
		
		String name = requestedName.toString();
		PersonSuggestionQueryBuilder query = this.logic.getPersonSuggestion(name).withEntityPersons(true).withNonEntityPersons(true).allowNamesWithoutEntities(false).withRelationType(PersonResourceRelationType.values());
		
		// [personID] List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations().byPersonId(person.getPersonId()).withPosts(true).withPersonsOfPosts(true).groupByInterhash(true).orderBy(ResourcePersonRelationQueryBuilder.Order.publicationYear).getIt();
		
		List<ResourcePersonRelation> suggestedPersons = query.doIt();
		
		// command.setPersonSuggestions(suggestedPersons);
		
		// find posts from suggested persons
		List<Post<?>> otherAuthorPosts;
		List<Post<?>> otherAdvisorPosts = new ArrayList<>();
		HashMap<ResourcePersonRelation, List<Post<?>>> suggestedPersonPosts = new HashMap<>();
		
		// fetch posts from each user with same name to the requested one
		for (final ResourcePersonRelation suggestedPerson : suggestedPersons) {
			List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations().byPersonId(suggestedPerson.getPerson().getPersonId()).withPosts(true).withPersonsOfPosts(true).groupByInterhash(true).orderBy(ResourcePersonRelationQueryBuilder.Order.publicationYear).getIt();
			otherAuthorPosts = new ArrayList<>();

			for (final ResourcePersonRelation resourcePersonRelation : resourceRelations) {
				// escape thesis
				final boolean isThesis = resourcePersonRelation.getPost().getResource().getEntrytype().toLowerCase().endsWith("thesis");
				if (isThesis)
					continue;

				// get posts from suggested persons				
				if (resourcePersonRelation.getRelationType().equals(PersonResourceRelationType.AUTHOR)) {
					// posts from a known author
					otherAuthorPosts.add(resourcePersonRelation.getPost());
				} else {
					// posts from an author with the same name but no assignment
					otherAdvisorPosts.add(resourcePersonRelation.getPost());
				}
			}
			suggestedPersonPosts.put(suggestedPerson, otherAuthorPosts);
		}
		
		command.setSuggestedPersonPosts(suggestedPersonPosts);
		command.setSuggestedPosts(otherAdvisorPosts);
		
		return Views.DISAMBIGUATION;
	}
	
	/**
	 * @param res
	 * @param requestedRole
	 * @return
	 */
	private static List<PersonName> getPersonsByFallBack(BibTex res, PersonResourceRelationType requestedRole) {
		switch (requestedRole) {
		case AUTHOR:
			return res.getPersonNamesByRole(PersonResourceRelationType.EDITOR);
		case EDITOR:
			return res.getPersonNamesByRole(PersonResourceRelationType.AUTHOR);
		default:
			return null;
		}
	}

	/**
	 * creates a new person, links te resource and redirects to the new person page
	 * @param command
	 * @return
	 */
	private View newAction(DisambiguationPageCommand command) {
		final Person person = createPersonEntity(command);
		try {
			linkToPerson(command, person);
		} catch (LogicException e) {
			command.getLogicExceptions().add(e);
			return disambiguateAction(command);
		}
		
		this.requestLogic.setLastAction(ACTION_KEY_CREATE_AND_LINK_PERSON);
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(person.getPersonId()));
	}

	private Person createPersonEntity(DisambiguationPageCommand command) {
		final PersonName mainName = getMainPersonName(command);
		
		final Person person = new Person();
		mainName.setMain(true);
		person.setMainName(mainName);
		this.logic.createOrUpdatePerson(person);
		command.setPerson(person);
		return person;
	}

	private void linkToPerson(DisambiguationPageCommand command, final Person person) throws ResourcePersonAlreadyAssignedException {
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		resourcePersonRelation.setPerson(person);
		resourcePersonRelation.setPost(command.getPost());
		resourcePersonRelation.setRelationType(command.getRequestedRole());
		resourcePersonRelation.setPersonIndex(command.getRequestedIndex().intValue());
		this.logic.addResourceRelation(resourcePersonRelation);
	}

	private static PersonName getMainPersonName(DisambiguationPageCommand command) {
		final BibTex bibtex = command.getPost().getResource();
		final List<PersonName> publicationNames = bibtex.getPersonNamesByRole(command.getRequestedRole());
		final int personIndex = command.getRequestedIndex().intValue();
		if ((publicationNames == null) || (publicationNames.size() <= personIndex)) {
			throw new IllegalArgumentException();
		}
		final PersonName mainName = publicationNames.get(personIndex);
		return mainName;
	}
	
	
	/**
	 * creates a new person, links the resource and redirects to the new person page
	 * @param command
	 * @return
	 */
	private View linkAction(DisambiguationPageCommand command) {
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getRequestedPersonId());
		try {
			linkToPerson(command, person);
		} catch (LogicException e) {
			command.getLogicExceptions().add(e);
			return disambiguateAction(command);
		}
		this.requestLogic.setLastAction(ACTION_KEY_LINK_PERSON);
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(person.getPersonId()));
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public void setPersonRoleRenderer(PersonRoleRenderer personRoleRenderer) {
		this.personRoleRenderer = personRoleRenderer;
	}
}