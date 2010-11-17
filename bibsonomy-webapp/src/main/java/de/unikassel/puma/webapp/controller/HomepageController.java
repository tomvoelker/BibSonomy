package de.unikassel.puma.webapp.controller;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.HomepageCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * TODO: remove as soon as the homepage controller is configurable
 * Controller for Homepage
 *
 * @author Dominik Benz
 * @version $Id$
 */
public class HomepageController extends SingleResourceListController implements MinimalisticController<HomepageCommand> {
	private static final int MAX_TAGS = 50;
	private static final Log log = LogFactory.getLog(HomepageController.class);

	@Override
	public View workOn(final HomepageCommand command) {
		/*
		 * FIXME: implement filter=no parameter
		 */
		log.debug(this.getClass().getSimpleName());
		log.info("PUMA:" + this.getClass().getSimpleName());
		final String format = command.getFormat();
		this.startTiming(this.getClass(), format);
		
		// handle the case when only tags are requested
		this.handleTagsOnly(command, GroupingEntity.ALL, null, null, null, null, MAX_TAGS, null);		
		
		// retrieve and set the requested resource lists
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {
			// disable manual setting of start value for home page
			command.getListCommand(resourceType).setStart(0);
			setList(command, resourceType, GroupingEntity.ALL, null, null, null, null, null, null, 5);
			postProcessAndSortList(command, resourceType);
		}
												
		// html format - retrieve tags and return HTML view
		if (format.equals("html")) {
			command.setPageTitle("home");
			setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, MAX_TAGS, null);

			/*
			 * add news posts (= latest blog posts) FIXME: make configurable
			 */
			command.setNews(this.logic.getPosts(Bookmark.class, GroupingEntity.GROUP, "puma", Arrays.asList("pumanews"), null, null, null, 0, 3, null));
			
			this.endTiming();
			return Views.PUMAHOMEPAGE;		
		}
		
		this.endTiming();
		// export - return the appropriate view
		return Views.getViewByFormat(format);	
	}

	/** Enforce 50 tags in the tag cloud.
	 * 
	 * @see org.bibsonomy.webapp.controller.ResourceListController#getFixedTagMax(int)
	 */
	@Override
	protected int getFixedTagMax(int tagMax) {
		return MAX_TAGS;
	}
	
	@Override
	public HomepageCommand instantiateCommand() {
		return new HomepageCommand();
	}
}
