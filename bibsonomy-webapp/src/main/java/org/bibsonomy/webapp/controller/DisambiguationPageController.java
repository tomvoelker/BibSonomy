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
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.DisambiguationPageCommand;
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
public class DisambiguationPageController extends SingleResourceListController implements MinimalisticController<DisambiguationPageCommand> {
	private static final Log log = LogFactory.getLog(DisambiguationPageController.class);
	
	private PersonLogic personLogic = new PersonLogic();
	
	@Override
	public View workOn(final DisambiguationPageCommand command) {
		
		/**
		 * person highest degree?
		 */
		command.setSuggestedPersons(new HashSet<Person>());
		command.setRequestedAuthorName("Stumme, Gert");
		Person p = new Person();
		p.setId(12334);
		p.setAcademicDegree("Professor");
		p.setMainName(new PersonName("Christian", "Pfeiffer"));
		command.getSuggestedPersons().add(p);
		PersonName pn = new PersonName();
		pn.setFirstName(command.getRequestedAuthorName().split(",")[1]);
		pn.setLastName(command.getRequestedAuthorName().split(",")[0]);
		command.setAuthorName(pn);
		
		switch(command.getRequestedAction()) {
			case "redirected" : return this.redirectedAction(command);
			case "link" : return this.linkAction(command);
			case "details" : return this.detailsAction(command);
			default: throw new MalformedURLSchemeException("Controller " + this.getClass().toString() + " cant handle action " + command.getRequestedAction());
		}
	}


	/**
	 * @param command
	 * @return
	 */
	private View detailsAction(DisambiguationPageCommand command) {

		/**
		 * TODO
		 * differ between 	intrahash+user
		 * 					interhash
		 * 
		 * 
		 */
		command.setPost(this.logic.getPostDetails(command.getRequestedHash(), command.getRequestedUser()));
		/**
		 * TODO where to get?
		 * command.setSuggestedPersons(suggestedPersons);
		 */
		
		return Views.DISAMBIGUATION;
	}


//	/**
//	 * @param command
//	 * @return
//	 */
//	private View showAction(DisambiguationPageCommand command) {
//		
//		// retrieve and set the requested resource lists, along with total
//		// counts
//		Class<? extends Resource> toRemove = null;
//		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
//			if(resourceType.getName().equals("org.bibsonomy.model.Bookmark")) {
//				toRemove = resourceType;
//				continue;
//			}
//			command.getResourcetype().remove(toRemove);
//			final ListCommand<?> listCommand = command.getListCommand(resourceType);
//			final int entriesPerPage = listCommand.getEntriesPerPage();
//			
//			this.setList(command, resourceType, GroupingEntity.USER, command.getRequestedUser(), command.getRequestedTagsList(), null, null, command.getFilter(), null, command.getStartDate(), command.getEndDate(), entriesPerPage);
//			this.postProcessAndSortList(command, resourceType);
//	
//			/*
//			 * set the post counts
//			 */
//			this.setTotalCount(command, resourceType, GroupingEntity.USER, command.getRequestedUser(), command.getRequestedTagsList(), null, null, null, null, null, command.getStartDate(), command.getEndDate(), entriesPerPage);
//		}
//		
//		return Views.DISAMBIGUATION;
//	}

	@Override
	public DisambiguationPageCommand instantiateCommand() {
		return new DisambiguationPageCommand();
	}
	
	private View redirectedAction(DisambiguationPageCommand command) {
		
		/** 
		 * TODO
		 * 1. 	Check if hash is interhash or intrahash
		 * 2. 	if intrahash, set current user to author of this post
		 * 			redirect to personPage
		 * 		else
		 * 			redirect to persondisambiguationPage		
		 *  
		 */
		
		//return new ExtendedRedirectView("/person/show/"+command.getRequestedAuthorName() + "/" + command.getRequestedHash());
		return new ExtendedRedirectView("/persondisambiguation/details/"+command.getRequestedAuthorName() + "/" + command.getRequestedHash());
	}
	
	private View linkAction(DisambiguationPageCommand command) {
		
		/**
		 * TODO
		 * 1. link resource + person
		 * 2. redirect
		 */
		
		return new ExtendedRedirectView("/persondisambiguation/details/"+command.getRequestedAuthorName());
	}
}


