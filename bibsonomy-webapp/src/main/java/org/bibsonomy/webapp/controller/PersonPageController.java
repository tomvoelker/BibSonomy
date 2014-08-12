package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.config.Parameters;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.PersonLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageController extends SingleResourceListControllerWithTags implements MinimalisticController<PersonPageCommand> {
	private static final Log log = LogFactory.getLog(PersonPageController.class);
	
	//TODO: get by injection
	private PersonLogic personLogic = new PersonLogic();
	
	@Override
	public View workOn(final PersonPageCommand command) {
		if (!present(command.getRequestedPersonId())) {
			throw new MalformedURLSchemeException("error.person_page_without_personname");
		} else if(!present(command.getRequestedPersonName())) {
			throw new MalformedURLSchemeException("error.person_page_without_personname");
		}
		
		switch(command.getRequestedAction()) {
			case "show": return this.showAction(command);
			case "update": return this.updateAction(command);
			case "addName": return this.addNameAction(command);
			case "details": return this.detailsAction(command);
			case "editRole": return this.editRoleAction(command);
			case "unlink": return this.unlinkAction(command);
			default: throw new MalformedURLSchemeException("error.controller cant handle action " + command.getRequestedAction());
		}
	}


	/**
	 * @param command
	 * @return
	 */
	private View unlinkAction(PersonPageCommand command) {
		
		//TODO: where is the person<->publication-relation stored?
		
		log.info("Accessed unlinkAction -> getting redirected");
		
		this.personLogic.updatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + "/" + command.getRequestedPersonName());
	}


	/**
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		
		//TODO where is role-relation stored??
		
		log.info("Accessed editRoleAction -> getting redirected");
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + "/" + command.getRequestedPersonName());
	}


	/**
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		
		command.getPerson().setAcademicDegree(command.getFormGraduation());
		command.getPerson().setMainName(command.getFormSelectedName());
		
		this.personLogic.updatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + "/" + command.getRequestedPersonName());
	}


	/**
	 * @param requestedPersonId
	 * @param user
	 * @param command
	 * @return 
	 */
	private View addNameAction(PersonPageCommand command) {
		
		command.getPerson().getAlternateNames().add(new PersonName(command.getFormGivenName(), command.getFormSurName()));
		
		this.personLogic.updatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + "/" + command.getRequestedPersonName());
	}


	/**
	 * @param command
	 * @return
	 */
	private View detailsAction(PersonPageCommand command) {

		// retrieve and set the requested resource lists, along with total
		// counts
		Class<? extends Resource> toRemove = null;
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
			if(resourceType.getName().equals("org.bibsonomy.model.Bookmark")) {
				toRemove = resourceType;
				continue;
			}
			command.getResourcetype().remove(toRemove);
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, GroupingEntity.USER, command.getRequestedUser(), command.getRequestedTagsList(), command.getRequestedHash(), null, command.getFilter(), null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			this.postProcessAndSortList(command, resourceType);
	
			/*
			 * set the post counts
			 */
			this.setTotalCount(command, resourceType, GroupingEntity.USER, command.getRequestedUser(), command.getRequestedTagsList(), command.getRequestedHash(), null, null, null, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
		}
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
	
			// retrieve concepts
			final List<Tag> concepts = this.logic.getConcepts(null, GroupingEntity.USER, command.getRequestedUser(), null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);
			command.getConcepts().setConceptList(concepts);
			
			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() >= Parameters.TAG_THRESHOLD) {
				log.debug("User " + command.getRequestedUser() + " has reached threshold of " + Parameters.TAG_THRESHOLD + " tags on user page");
			}
	
			return Views.PERSON_SINGLE;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
	}

	/**
	 * @param command
	 * @return
	 */
	private View showAction(PersonPageCommand command) {
		// retrieve and set the requested resource lists, along with total
		// counts
		command.setRequestedUser(command.getRequestedPersonId());
		Class<? extends Resource> toRemove = null;
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
			if(resourceType.getName().equals("org.bibsonomy.model.Bookmark")) {
				toRemove = resourceType;
				continue;
			}
			command.getResourcetype().remove(toRemove);
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, GroupingEntity.USER, command.getRequestedUser(), command.getRequestedTagsList(), command.getRequestedHash(), null, command.getFilter(), null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			this.postProcessAndSortList(command, resourceType);
	
			/*
			 * set the post counts
			 */
			this.setTotalCount(command, resourceType, GroupingEntity.USER, command.getRequestedUser(), command.getRequestedTagsList(), command.getRequestedHash(), null, null, null, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
		}
		
		// html format - retrieve tags and return HTML view
		if ("html".equals(command.getFormat())) {
	
			// retrieve concepts
			final List<Tag> concepts = this.logic.getConcepts(null, GroupingEntity.USER, command.getRequestedUser(), null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);
			command.getConcepts().setConceptList(concepts);
			
			// log if a user has reached threshold
			if (command.getTagcloud().getTags().size() >= Parameters.TAG_THRESHOLD) {
				log.debug("User " + command.getRequestedUser() + " has reached threshold of " + Parameters.TAG_THRESHOLD + " tags on user page");
			}
	
			return Views.PERSON_ALL;
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());
	}

	@Override
	public PersonPageCommand instantiateCommand() {
		return new PersonPageCommand();
	}
}


