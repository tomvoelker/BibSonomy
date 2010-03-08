package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.BibtexResourceViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller for the basket page
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class BasketPageController extends SingleResourceListController implements MinimalisticController<BibtexResourceViewCommand>, ErrorAware {

	private Errors errors;

	public View workOn(BibtexResourceViewCommand command) {

		this.startTiming(this.getClass(), command.getFormat());

		// if user is not logged in, redirect him to login page
		if (command.getContext().isUserLoggedIn() == false) {
			errors.reject("error.general.login");
			return Views.ERROR;
		}				

		// set login user name + grouping entity = BASKET
		final String loginUserName = command.getContext().getLoginUser().getName();
		final GroupingEntity groupingEntity = GroupingEntity.BASKET;

		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());

		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : listsToInitialise) {			
			final int entriesPerPage = command.getListCommand(resourceType).getEntriesPerPage();

			this.setList(command, resourceType, groupingEntity, loginUserName, null, null, null, null, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);

			/*
			 * set all posts from basket page to "picked" such that their "pick"
			 * link changes to "unpick"
			 */
			for (final Post<? extends Resource> post : command.getListCommand(resourceType).getList()){
				post.setPicked(true);
			}

			// the number of items in this user's basket has already been fetched
			command.getListCommand(resourceType).setTotalCount(command.getContext().getLoginUser().getBasket().getNumPosts());
		}	

		this.endTiming();			
		if ("html".equals(command.getFormat())) {
			command.setPageTitle(" :: basket" );
			return Views.BASKETPAGE;	
		}

		// export - return the appropriate view
		return Views.getViewByFormat(command.getFormat());

	}

	public BibtexResourceViewCommand instantiateCommand() {
		return new BibtexResourceViewCommand();
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
