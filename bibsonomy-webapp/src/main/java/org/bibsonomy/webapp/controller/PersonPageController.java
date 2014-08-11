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
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.config.Parameters;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageController extends SingleResourceListControllerWithTags implements MinimalisticController<PersonPageCommand>, RequestAware {
	private static final Log log = LogFactory.getLog(PersonPageController.class);
	private RequestLogic requestLogic;
	
	@Override
	public View workOn(final PersonPageCommand command) {
		System.out.println(command.getRequestedAction());
		System.out.println(command.getFormGivenName());
		System.out.println(command.getFormGraduation());
		if (!present(command.getRequestedPersonId())) {
			throw new MalformedURLSchemeException("error.person_page_without_personname");
		} else if(!present(command.getRequestedPersonName())) {
			throw new MalformedURLSchemeException("error.person_page_without_personname");
		}
		
		if(command.getRequestedAction() != null) {
			if(command.getRequestedAction().equals("newPersonName")) {
				this.addNameAction(command.getRequestedPersonId(), command.getUser(), command);
				return this.showAllAction(command);
			} else if(command.getRequestedAction().equals("savePerson")) {
				this.updatePersonAction(command);
				return this.showAllAction(command);
			}
		} else if(command.getRequestedHash() != null) {
			return this.showSingleAction(command);
		}
		
		return this.showAllAction(command);
	}


	/**
	 * @param command
	 */
	private void updatePersonAction(PersonPageCommand command) {
		Person person = new Person();
		person.setGraduation(command.getFormGraduation());
		command.setPerson(person);
	}


	/**
	 * @param requestedPersonId
	 * @param user
	 * @param command
	 */
	private void addNameAction(String requestedPersonId, User user,
			PersonPageCommand command) {
		
		Set<PersonName> names = new HashSet<PersonName>();
		names.add(new PersonName(command.getFormGivenName(), command.getFormSurName()));
		Person person = new Person();
		person.setNames(names);
		command.setPerson(person);		
	}


	/**
	 * @param command
	 * @return
	 */
	private View showSingleAction(PersonPageCommand command) {

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
	private View showAllAction(PersonPageCommand command) {
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


	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.RequestAware#setRequestLogic(org.bibsonomy.webapp.util.RequestLogic)
	 */
	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
		
	}
}


