/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.exception.LogicException;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.person.PersonRoleRenderer;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.validation.Errors;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageController extends SingleResourceListController implements MinimalisticController<PersonPageCommand>, ErrorAware {
	
	private RequestLogic requestLogic;
	private PersonRoleRenderer personRoleRenderer;
	private Errors errors;
	
	
	@Override
	public View workOn(final PersonPageCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}
		
		final String formAction = command.getFormAction();
		if (present(formAction)) {
			switch(formAction) {
				case "update": return this.updateAction(command);
				case "addName": return this.addNameAction(command);
				case "deleteName": return this.deleteNameAction(command);
				case "addRole": return this.addRoleAction(command);
				case "addThesis": return this.addThesisAction(command);
				case "editRole": return this.editRoleAction(command);
				case "deleteRole": return this.deleteRoleAction(command);
				case "unlink": return this.unlinkAction(command);
				case "link": return this.linkAction(command);
				case "search": return this.searchAction(command);
				case "searchAuthor": return this.searchAuthorAction(command);
				case "searchPub": return this.searchPubAction(command);
				default: return indexAction();
			}
		} else if (present(command.getRequestedPersonId())) {
			return this.showAction(command);
		}
		return indexAction();
	}

	/**
	 * @param command
	 * @return
	 */
	private View addThesisAction(PersonPageCommand command) {
		addRoleAction(command);
		return showAction(command);
	}

	/**
	 * @param command
	 * @return
	 */
	private View searchAuthorAction(PersonPageCommand command) { 
		final List<ResourcePersonRelation> suggestions = this.logic.getPersonSuggestion(command.getFormSelectedName()).withNonEntityPersons(true).withRelationType(PersonResourceRelationType.AUTHOR).preferUnlinked(true).doIt();
		
		JSONArray array = new JSONArray();
		for (ResourcePersonRelation rel : suggestions) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("interhash", rel.getPost().getResource().getInterHash());
			jsonPersonName.put("personIndex", rel.getPersonIndex());
			//jsonPersonName.put("personNameId", personName.getPersonChangeId());
			final BibTex pub = rel.getPost().getResource();
			final List<PersonName> authors = pub.getAuthor();
			jsonPersonName.put("personName", BibTexUtils.cleanBibTex(authors.get(rel.getPersonIndex()).toString()));
			jsonPersonName.put("extendedPublicationName", this.personRoleRenderer.getExtendedPublicationName(pub, this.requestLogic.getLocale(), false));
			
			array.add(jsonPersonName);
		}
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}
	
	private View searchPubAction(PersonPageCommand command) { 
		final List<Post<BibTex>> suggestions = this.logic.getPublicationSuggestion(command.getFormSelectedName());
		
		JSONArray array = new JSONArray();
		for (Post<BibTex> pub : suggestions) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("interhash", pub.getResource().getInterHash());
			jsonPersonName.put("extendedPublicationName", this.personRoleRenderer.getExtendedPublicationName(pub.getResource(), this.requestLogic.getLocale(), false));
			array.add(jsonPersonName);
		}
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}



	/**
	 * @param command
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private View searchAction(PersonPageCommand command) {
		final List<ResourcePersonRelation> suggestions = this.logic.getPersonSuggestion(command.getFormSelectedName()).withEntityPersons(true).withNonEntityPersons(true).allowNamesWithoutEntities(false).withRelationType(PersonResourceRelationType.values()).doIt();
		
		JSONArray array = new JSONArray();
		for (ResourcePersonRelation rel : suggestions) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("personId", rel.getPerson().getPersonId());
			//jsonPersonName.put("personNameId", personName.getPersonChangeId());
			jsonPersonName.put("personName", BibTexUtils.cleanBibTex(rel.getPerson().getMainName().toString()));
			jsonPersonName.put("extendedPersonName", this.personRoleRenderer.getExtendedPersonName(rel, this.requestLogic.getLocale(), false));
			
			array.add(jsonPersonName);
		}
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}

	private static View indexAction() {
		return Views.PERSON_SHOW;
	}

	/**
	 * Action called when a user want to unlink an author from a publication
	 * @param command
	 * @return
	 */
	private View unlinkAction(PersonPageCommand command) {
		this.logic.unlinkUser(this.logic.getAuthenticatedUser().getName());
		return Views.AJAX_TEXT;
	}
	
	private View linkAction(PersonPageCommand command) {
		this.logic.linkUser(command.getFormPersonId());
		return Views.AJAX_TEXT;
	}
	
	/**
	 * Action called when a user wants to add a person role to a thesis
	 * @param command
	 * @return
	 */
	private View addRoleAction(PersonPageCommand command) {
		final JSONObject jsonResponse = new JSONObject();
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		final Post<BibTex> post = new Post<>();
		post.setResource(new BibTex());
		post.getResource().setInterHash(command.getFormInterHash());
		resourcePersonRelation.setPost(post);
		
		try {
			Person person = new Person();
			if (present(command.getFormPersonId())) {
				person.setPersonId(command.getFormPersonId());
			} else {
				final PersonName mainName = new PersonName();
				mainName.setMain(true);
				mainName.setFirstName(command.getFormFirstName());
				mainName.setLastName(command.getFormLastName());
				person.setMainName(mainName);
				this.logic.createOrUpdatePerson(person);
			}
			resourcePersonRelation.setPerson(person);
			resourcePersonRelation.setPersonIndex(command.getFormPersonIndex());
			resourcePersonRelation.setRelationType(command.getFormPersonRole());

			this.logic.addResourceRelation(resourcePersonRelation);
		} catch (LogicException e) {
			command.getLogicExceptions().add(e);
			jsonResponse.put("exception", e.getClass().getSimpleName());
		}

		jsonResponse.put("personId", resourcePersonRelation.getPerson().getPersonId());
		jsonResponse.put("resourcePersonRelationid", resourcePersonRelation.getPersonRelChangeId() + "");
		jsonResponse.put("personUrl", new URLGenerator().getPersonUrl(resourcePersonRelation.getPerson().getPersonId()));
		command.setResponseString(jsonResponse.toJSONString());
		
		return Views.AJAX_JSON;
	}

	/**
	 * Action called when a user wants to edit the role of a person in a thesis
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		//TODO not used? remove?
		for (String role : command.getFormPersonRoles()) {
			final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
			Post<BibTex> post = new Post<>();
			post.setResource(new BibTex());
			post.getResource().setInterHash(command.getFormInterHash());
			resourcePersonRelation.setPost(post);
			resourcePersonRelation.setPerson(new Person());
			resourcePersonRelation.getPerson().setPersonId(command.getFormPersonId());
			resourcePersonRelation.setPersonIndex(command.getFormPersonIndex());
			final PersonResourceRelationType relationType = PersonResourceRelationType.valueOf(StringUtils.upperCase(role)); 
			resourcePersonRelation.setRelationType(relationType);
			try {
				this.logic.addResourceRelation(resourcePersonRelation);
			} catch (LogicException e) {
				command.getLogicExceptions().add(e);
			}
		}
		
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getPersonId()));	
	}
	
	@SuppressWarnings("boxing")
	private View deleteRoleAction(PersonPageCommand command) {

		this.logic.removeResourceRelation(Integer.valueOf(command.getFormResourcePersonRelationId()));
				
		return Views.AJAX_TEXT;	
	}

	/**
	 * Action called when a user updates preferences of a person
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		command.setPerson(this.logic.getPersonById(PersonIdType.BIBSONOMY_ID, command.getFormPersonId()));
		if (command.getPerson() == null) {
			// TODO: proper frontend responses in cases like this
			throw new NoSuchElementException();
		}
		command.getPerson().setAcademicDegree(command.getFormAcademicDegree());
		command.getPerson().getMainName().setMain(false);
		command.getPerson().setMainName(Integer.parseInt(command.getFormSelectedName()));
		command.getPerson().setOrcid(command.getFormOrcid());
		command.getPerson().setUser(command.isFormThatsMe() ? AuthenticationUtils.getUser().getName() : null);
		this.logic.createOrUpdatePerson(command.getPerson());
		
		return Views.AJAX_TEXT;
	}

	/**
	 * Action called when a user adds an alternative name to a person
	 * @param command
	 */
	private View addNameAction(PersonPageCommand command) {
		final Person person = logic.getPersonById(PersonIdType.BIBSONOMY_ID, command.getFormPersonId());
		
		final PersonName personName = new PersonName(command.getFormLastName());
		personName.setFirstName(command.getFormFirstName());
		personName.setPersonId(command.getFormPersonId());
		
		for (PersonName otherName : person.getNames()) {
			if (personName.equals(otherName)) {
				command.setResponseString(otherName.getPersonNameChangeId()+ "");
				return Views.AJAX_TEXT;
			}
		}
		this.logic.createPersonName(personName);
		command.setResponseString(Integer.toString(personName.getPersonNameChangeId()));
		
		return Views.AJAX_TEXT;
	}

	/**
	 * Action called when a user removes an alternative name from a person
	 * @param command
	 * @return
	 */
	private View deleteNameAction(PersonPageCommand command) {
		this.logic.removePersonName(new Integer(command.getFormPersonNameId()));
		return Views.AJAX_TEXT;
	}
	
	/**
	 * Default action called when a user url is called
	 * @param command
	 * @return
	 */
	private View showAction(PersonPageCommand command) {
		
		for(PersonResourceRelationType prr : PersonResourceRelationType.values()) {
			command.getAvailableRoles().add(prr);
		}
		
		command.setPerson(this.logic.getPersonById(PersonIdType.BIBSONOMY_ID, command.getRequestedPersonId()));
		
		if (DisambiguationPageController.ACTION_KEY_CREATE_AND_LINK_PERSON.equals(this.requestLogic.getLastAction()) || DisambiguationPageController.ACTION_KEY_LINK_PERSON.equals(this.requestLogic.getLastAction())) {
			command.setOkHintKey(this.requestLogic.getLastAction());
			this.requestLogic.setLastAction(null);
		}
		
		List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations().byPersonId(command.getPerson().getPersonId()).withPosts(true).withPersonsOfPosts(true).groupByInterhash(true).orderBy(ResourcePersonRelationQueryBuilder.Order.publicationYear).getIt();
		List<Post<?>> authorPosts = new ArrayList<>();
		List<Post<?>> advisorPosts = new ArrayList<>();
		List<Post<?>> otherAuthorPosts = new ArrayList<>();
		List<Post<?>> otherAdvisorPosts = new ArrayList<>();

		for(ResourcePersonRelation resourcePersonRelation : resourceRelations) {
			final boolean isThesis = resourcePersonRelation.getPost().getResource().getEntrytype().toLowerCase().endsWith("thesis");
			
			if (resourcePersonRelation.getRelationType().equals(PersonResourceRelationType.AUTHOR)) {
				if (isThesis) {
					authorPosts.add(resourcePersonRelation.getPost());
				} else {
					otherAuthorPosts.add(resourcePersonRelation.getPost());
				}
			} else {
				if (isThesis) {
					advisorPosts.add(resourcePersonRelation.getPost());
				} else {
					otherAdvisorPosts.add(resourcePersonRelation.getPost());
				}
			}
			
			// we explicitly do not want ratings on the person pages because this might cause users of the genealogy feature to hesitate putting in their dissertations
			resourcePersonRelation.getPost().getResource().setRating(null);
			resourcePersonRelation.getPost().getResource().setNumberOfRatings(null);
		}
		
		command.setThesis(authorPosts);
		command.setOtherPubs(otherAuthorPosts);
		command.setAdvisedThesis(advisorPosts);
		command.setOtherAdvisedPubs(otherAdvisorPosts);
		
		return Views.PERSON_SHOW;
	}
	
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	@Override
	public PersonPageCommand instantiateCommand() {
		return new PersonPageCommand();
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public void setPersonRoleRenderer(PersonRoleRenderer personRoleRenderer) {
		this.personRoleRenderer = personRoleRenderer;
	}
}


