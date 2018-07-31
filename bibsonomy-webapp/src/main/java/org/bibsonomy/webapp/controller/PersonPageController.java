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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonMergeFieldConflict;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Gender;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.exception.LogicException;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.person.PersonRoleRenderer;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageController extends SingleResourceListController implements MinimalisticController<PersonPageCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(PersonMatch.class);
	private RequestLogic requestLogic;
	private PersonRoleRenderer personRoleRenderer;
	private Errors errors;
	
	
	@Override
	public View workOn(final PersonPageCommand command) {
		final RequestWrapperContext context = command.getContext();
		final String formAction = command.getFormAction();
		if (!present(formAction) && !present(command.getRequestedPersonId())){
			throw new MalformedURLSchemeException("The person page was requested without a person in the request.");
		}
		
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
		}
		
		if (present(formAction)) {
			switch(formAction) {
				case "conflictMerge": return this.conflictMerge(command);
				case "getConflict": return this.getConflicts(command);
				case "update": return this.updateAction(command);
				case "addName": return this.addNameAction(command);
				case "deleteName": return this.deleteNameAction(command);
				case "setMainName": return this.setMainNameAction(command);
				case "addRole": return this.addRoleAction(command);
				case "addThesis": return this.addThesisAction(command);
				case "editRole": return this.editRoleAction(command);
				case "deleteRole": return this.deleteRoleAction(command);
				case "unlink": return this.unlinkAction(command);
				case "link": return this.linkAction(command);
				case "search": return this.searchAction(command);
				case "searchAuthor": return this.searchAuthorAction(command);
				case "searchPub": return this.searchPubAction(command);
				case "merge": return this.mergeAction(command);
				case "searchPubAuthor": return this.searchPubAuthorAction(command);

				default: return indexAction();
			}
		} else if (present(command.getRequestedPersonId())) {
			return this.showAction(command);
		}
		
		// the following statement cannot be reached, and seems useless anyway, since in this case no formAction was present and not PersonId. 
		// Remove when sure. 
		return indexAction();
	}

	/**
	 * @param command
	 * @return
	 */
	private View conflictMerge(PersonPageCommand command) {
		try {
			
			Map<String, String> map = new HashMap<String, String>();
			if(command.getPerson()!=null){
				for (String fieldName : Person.fieldsWithResolvableMergeConflicts){
					PropertyDescriptor desc = new PropertyDescriptor(fieldName, Person.class);
					Object value = desc.getReadMethod().invoke(command.getPerson());
					if (value != null){
						if (fieldName == "gender") {
							map.put("gender", ((Gender) value).toString());
						} else if (fieldName != "mainName"){
							map.put(fieldName, (String) value);
						}
					}
				}
			}
			if(command.getNewName()!=null){
				map.put("mainName", ((PersonName) command.getNewName()).getLastName() + ", " + ((PersonName) command.getNewName()).getFirstName());
			}
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("status", this.logic.conflictMerge(command.getFormMatchId(), map));
			command.setResponseString(jsonResponse.toString());
			
			return Views.AJAX_JSON;
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("status", false);
		command.setResponseString(jsonResponse.toString());
		
		return Views.AJAX_JSON;
	}

	/**
	 * @param command
	 * @return
	 */
	private View getConflicts(PersonPageCommand command) {
		List<PersonMatch> list = new LinkedList<PersonMatch>();
		list.add(this.logic.getPersonMatch(command.getFormMatchId()));
		
		JSONArray array = new JSONArray();
		for (PersonMergeFieldConflict conflict : PersonMatch.getMergeConflicts(list).get(command.getFormMatchId())){
			JSONObject jsonConflict = new JSONObject();
			jsonConflict.put("field", conflict.getFieldName());
			jsonConflict.put("person1Value", conflict.getPerson1Value());
			jsonConflict.put("person2Value", conflict.getPerson2Value());
			array.add(jsonConflict);
		}
		command.setResponseString(array.toJSONString());
		return Views.AJAX_JSON;
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
	 * This is a helper function that adds to an JSONarray from a list of resource-person-relations.
	 * @param suggestions
	 * @param array
	 * @return
	 */
	private void buildupAuthorResponseArray(final List<ResourcePersonRelation> suggestions, JSONArray array) {
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
	}
	
	/** 
	 * This is a helper function adds to an JSONarray Publications form a sugesstions list.  
	 * @param suggestions
	 * @param array
	 * @return
	 */	
	private void buildupPubResponseArray(final List<Post<BibTex>> suggestions, JSONArray array) {
		for (final Post<BibTex> pub : suggestions) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("interhash", pub.getResource().getInterHash());
			jsonPersonName.put("extendedPublicationName", this.personRoleRenderer.getExtendedPublicationName(pub.getResource(), this.requestLogic.getLocale(), false));
			array.add(jsonPersonName);
		}
	}
	
	/**
	 * @param command
	 * @return
	 */
	private View searchAuthorAction(PersonPageCommand command) { 
		final List<ResourcePersonRelation> suggestions = this.logic.getPersonSuggestion(command.getFormSelectedName()).withEntityPersons(true).withNonEntityPersons(true).withRelationType(PersonResourceRelationType.AUTHOR).preferUnlinked(true).doIt();
		JSONArray array = new JSONArray();
		buildupAuthorResponseArray(suggestions,array);
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}
	
	/**
	 * @param command
	 * @return
	 */
	private View searchPubAction(PersonPageCommand command) { 
		final List<Post<BibTex>> suggestions = this.logic.getPublicationSuggestion(command.getFormSelectedName());
		
		JSONArray array = new JSONArray();
		for (final Post<BibTex> pub : suggestions) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("interhash", pub.getResource().getInterHash());
			jsonPersonName.put("extendedPublicationName", this.personRoleRenderer.getExtendedPublicationName(pub.getResource(), this.requestLogic.getLocale(), false));
			array.add(jsonPersonName);
		}
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}

	/**
	 * Combined publication and author search action. This search is in particular necessary 
	 * when someone want's to find unrelated (no role associated to authors) documents.  
	 * @param command
	 * @return
	 */
	private View searchPubAuthorAction(PersonPageCommand command) { 
		final List<ResourcePersonRelation> suggestionsPerson = this.logic.getPersonSuggestion(command.getFormSelectedName()).withEntityPersons(true).withNonEntityPersons(true).withRelationType(PersonResourceRelationType.AUTHOR).preferUnlinked(true).doIt();
		final List<Post<BibTex>> suggestionsPub = this.logic.getPublicationSuggestion(command.getFormSelectedName());
		
		JSONArray array = new JSONArray();
		buildupAuthorResponseArray(suggestionsPerson, array); // Person(with publication) oriented search return 
		buildupPubResponseArray(suggestionsPub, array);  // Publications(not associated to Persons) oriented search return
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
		
		final JSONArray array = new JSONArray();
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
				final PersonName mainName = command.getNewName();
				mainName.setMain(true);
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
		// TODO not used? remove?
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
	
	private View deleteRoleAction(PersonPageCommand command) {
		this.logic.removeResourceRelation(Integer.valueOf(command.getFormResourcePersonRelationId()).intValue());
		
		return Views.AJAX_TEXT;
	}

	/*
	 * performs the merge action for the selected match
	 */
	private View mergeAction(PersonPageCommand command) {
		int id = command.getFormMatchId();
		JSONObject jsonResponse = new JSONObject();

		PersonMatch match = this.logic.getPersonMatch(id);
		boolean result = true;
		if (command.getUpdateOperation() == PersonUpdateOperation.MERGE_ACCEPT) {
			result = this.logic.acceptMerge(match);
		} else if(command.getUpdateOperation() == PersonUpdateOperation.MERGE_DENIED) {
			this.logic.denieMerge(match);
		}
		jsonResponse.put("status", result);
		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;
	}
	
	/**
	 * Action called when a user updates preferences of a person
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getFormPersonId());
		
		if (command.getPerson() == null) {
			// FIXME: proper frontend responses in cases like this
			throw new NoSuchElementException();
		}
		
		
		PersonUpdateOperation operation = command.getUpdateOperation();
		JSONObject jsonResponse = new JSONObject();
		
		// set all attributes that might be updated
		person.setAcademicDegree(command.getPerson().getAcademicDegree());
		person.setOrcid(command.getPerson().getOrcid().replaceAll("-", ""));
		person.setResearcherid(command.getPerson().getResearcherid());
		person.setCollege(command.getPerson().getCollege());
		
		// TODO only allow updates if the editor "is" this person
		person.setEmail(command.getPerson().getEmail());
		person.setHomepage(command.getPerson().getHomepage());
		
		// FIXME: write independent update method
		// FIXME: add its me action
		
		//command.getPerson().getMainName().setMain(false);
		//command.getPerson().setMainName(Integer.parseInt(command.getFormSelectedName()));

		// bind the new person
		command.setPerson(person);
		
		// ???
		//command.getPerson().setUser(command.isFormThatsMe() ? AuthenticationUtils.getUser().getName() : null);
				
		try {	
			if (operation != null) {
				this.logic.updatePerson(command.getPerson(), operation);
			} else {						
				// standard
				this.logic.createOrUpdatePerson(command.getPerson());
			}	
		} catch (Exception e) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Some error occured");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}	
		jsonResponse.put("status", true);
		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;
	}

	/**
	 * Action called when a user adds an alternative name to a person
	 * @param command
	 */
	private View addNameAction(PersonPageCommand command) {
		final Person person = logic.getPersonById(PersonIdType.PERSON_ID, command.getPerson().getPersonId());

		final JSONObject jsonResponse = new JSONObject();

		if (!present(person) || !present(command.getNewName())) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Person cannot be found.");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}
		
		
		final PersonName personName = command.getNewName();
		personName.setPersonId(command.getPerson().getPersonId());
		
		for (PersonName otherName : person.getNames()) {
			if (personName.equals(otherName)) {
				//command.setResponseString(otherName.getPersonNameChangeId()+ "");
				jsonResponse.put("status", true);
				jsonResponse.put("personNameChangeId", otherName.getPersonNameChangeId());
				command.setResponseString(jsonResponse.toString());
				return Views.AJAX_JSON;
			}
		}
		
		try {			
			this.logic.createPersonName(personName);
		} catch (Exception e) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Some error occured");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}
		
		jsonResponse.put("status", true);
		jsonResponse.put("personNameChangeId", personName.getPersonNameChangeId());
		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;		
	}

	/**
	 * Action called when a user removes an alternative name from a person
	 * @param command
	 * @return
	 */
	private View deleteNameAction(PersonPageCommand command) {
		final JSONObject jsonResponse = new JSONObject();
		try {			
			this.logic.removePersonName(new Integer(command.getFormPersonNameId()));
		} catch (Exception e) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Some error occured");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}
		
		jsonResponse.put("status", true);
		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;	
	}
	
	
	private View setMainNameAction(PersonPageCommand command) {
		final Person person = logic.getPersonById(PersonIdType.PERSON_ID, command.getPerson().getPersonId());
		
		final JSONObject jsonResponse = new JSONObject();
		
		
		person.getMainName().setMain(false);
		person.setMainName(Integer.parseInt(command.getFormSelectedName()));
		
		// bind the new person
		command.setPerson(person);
		
		try {			
			this.logic.updatePerson(person, PersonUpdateOperation.UPDATE_NAMES);
		} catch (Exception e) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Some error occured");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}
		
		jsonResponse.put("status", true);
		command.setResponseString(jsonResponse.toString());
		
		return Views.AJAX_JSON;	
	}
	
	
	/**
	 * Default action called when a user url is called
	 * @param command
	 * @return
	 */
	private View showAction(PersonPageCommand command) {
		for (PersonResourceRelationType prr : PersonResourceRelationType.values()) {
			command.getAvailableRoles().add(prr);
		}

		// FIXME: this should be done in the logic not here
		String forwardId = this.logic.getForwardId(command.getRequestedPersonId());
		if (present(forwardId)) {
			command.setRequestedPersonId(forwardId);
		}
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getRequestedPersonId());
		
		if (!present(person)) {
			return Views.ERROR404;
		}
		command.setPerson(person);
		
		if (DisambiguationPageController.ACTION_KEY_CREATE_AND_LINK_PERSON.equals(this.requestLogic.getLastAction()) || DisambiguationPageController.ACTION_KEY_LINK_PERSON.equals(this.requestLogic.getLastAction())) {
			command.setOkHintKey(this.requestLogic.getLastAction());
			this.requestLogic.setLastAction(null);
		}
		
		List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations().byPersonId(person.getPersonId()).withPosts(true).withPersonsOfPosts(true).groupByInterhash(true).orderBy(ResourcePersonRelationQueryBuilder.Order.publicationYear).getIt();
		List<Post<?>> authorPosts = new ArrayList<>();
		List<Post<?>> advisorPosts = new ArrayList<>();
		List<Post<?>> otherAuthorPosts = new ArrayList<>();
		List<Post<?>> otherAdvisorPosts = new ArrayList<>();

		for (final ResourcePersonRelation resourcePersonRelation : resourceRelations) {
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
		command.setPersonMatchList(this.logic.getPersonMatches(person.getPersonId()));
		command.setMergeConflicts(PersonMatch.getMergeConflicts(command.getPersonMatchList()));
		
		final List<Post<BibTex>> similarAuthorPubs = this.getPublicationsOfSimilarAuthor(person);

		command.setSimilarAuthorPubs(similarAuthorPubs);
		
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
	
	private List<Post<BibTex>> getPublicationsOfSimilarAuthor(Person person) {
		
		final PersonName requestedName = person.getMainName();		
		final String name = person.getMainName().toString();
		
		PersonSuggestionQueryBuilder query = this.logic.getPersonSuggestion(name).withEntityPersons(true).withNonEntityPersons(true).allowNamesWithoutEntities(false).withRelationType(PersonResourceRelationType.values());
		List<ResourcePersonRelation> suggestedPersons = query.doIt();		
			
		/*
		 * FIXME: use author-parameter in getPosts method
		 * @see bibsonomy.database.managers.PostDatabaseManager.#getPostsByResourceSearch()
		 * 
		 * get at least 50 publications from authors with same name
		 */	
		final List<Post<BibTex>> pubAuthorSearch = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, name, SearchType.LOCAL, null , Order.ALPH, null, null, 0, 50);

		List<Post<BibTex>> pubsWithSameAuthorName = new ArrayList<>(pubAuthorSearch);
		for (final Post<BibTex> post : pubAuthorSearch) {
			try {
				// remove post from search if the author has not exactly the same sur- and last-name
				if (!present(post.getResource().getAuthor()) 
						|| !post.getResource().getAuthor().contains(requestedName)) {
					pubsWithSameAuthorName.remove(post);
				}
			} catch (Exception ex) {
				// remove the post
				pubsWithSameAuthorName.remove(post);
			}
		}
		
		List<Post<?>> postsOfSuggestedPersons = new ArrayList<>();
		HashMap<ResourcePersonRelation, List<Post<?>>> suggestedPersonPosts = new HashMap<>();

		// get all persons with same name
		for (final ResourcePersonRelation suggestedPerson : suggestedPersons) {

			List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations().byPersonId(suggestedPerson.getPerson().getPersonId()).orderBy(ResourcePersonRelationQueryBuilder.Order.publicationYear).getIt();
			List<Post<?>> personPosts = new ArrayList<>();
			
			for (final ResourcePersonRelation resourcePersonRelation : resourceRelations) {
				// escape thesis of person
				final boolean isThesis = resourcePersonRelation.getPost().getResource().getEntrytype().toLowerCase().endsWith("thesis");
				if (isThesis)
					continue;

				// get pub from the known person			
				if (resourcePersonRelation.getRelationType().equals(PersonResourceRelationType.AUTHOR)) {
					personPosts.add(resourcePersonRelation.getPost());
					postsOfSuggestedPersons.add(resourcePersonRelation.getPost());
				}
			}
			suggestedPersonPosts.put(suggestedPerson, personPosts);
		}

		// update the post-list from the search result
		// FIXME: this should be redone once the author-parameter is used
		List<Post<BibTex>> noPersonRelPubList = new ArrayList<>(pubsWithSameAuthorName);
		for (final Post<BibTex> post : pubsWithSameAuthorName) {
			final String currentPostInterHash = post.getResource().getInterHash();

			// remove post if it's already related to a person
			for (final Post<?> personPost : postsOfSuggestedPersons) {				
				if (currentPostInterHash.equals(personPost.getResource().getInterHash())) {
					noPersonRelPubList.remove(post);
					break;
				}
			}
		}
				
		return noPersonRelPubList;
	}
	
}


