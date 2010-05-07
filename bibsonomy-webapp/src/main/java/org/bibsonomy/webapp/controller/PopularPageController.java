package org.bibsonomy.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.MultiResourceViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * 
 * @author mwa
 * @version $Id$
 */
public class PopularPageController extends MultiResourceListController implements MinimalisticController<MultiResourceViewCommand>{
	private static final Log log = LogFactory.getLog(PopularPageController.class);
	
	private static final int MAX_TAGS = 50;
	
	
	protected Integer entriesPerPage;
	
	/**
	 * @return the entriesPerPage
	 */
	public Integer getEntriesPerPage() {
		return this.entriesPerPage;
	}

	/**
	 * @param entriesPerPage the entriesPerPage to set
	 */
	public void setEntriesPerPage(final Integer entriesPerPage) {
		this.entriesPerPage = entriesPerPage;
	}

	@Override
	public View workOn(final MultiResourceViewCommand command) {
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
				
		// set the grouping entity and the order
		final GroupingEntity groupingEntity = GroupingEntity.ALL;
		final Order order = Order.POPULAR;
		
		// the start parameter for OFFSET
		int begin = 0; 
		
		// the value of the field 'popular_days' in the database
		int days = 0;
		
		// the system tags
		final List<String> tags = new ArrayList<String>();
		// insert an empty tag
		tags.add("");
				
		// determine which lists to initalize depending on the output format 
		// and the requested resourcetype
		this.chooseListsToInitialize(command.getFormat(), command.getResourcetype());
		do {
			for (final Class<? extends Resource> resourceType : listsToInitialise) {
				// overwrite with systemtag
				tags.set(0, SystemTagsUtil.buildSystemTagString(SystemTags.DAYS, begin));
				// determine the value of popular days, e.g. the last 10 days
				days = this.logic.getPostStatistics(resourceType, groupingEntity, null, tags, null, order, null, 0, this.getEntriesPerPage(), null, null);
				
				// only retrieve and set the requested resource lists if days > 0
				// because otherwise the lists will be empty
				if (days > 0) {
					// retrieve and set the requested resource lists
					this.addList(command, resourceType, groupingEntity, null, tags, null, order, null, null, this.getEntriesPerPage());
					// FIXME: do this only once outside the "days"-loop
					this.postProcessAndSortList(command, resourceType);
					this.addDescription(command, resourceType, days + "");
				}
			}
			
			begin++;
		} while (days > 0 && begin < 10); //show max 10 entries

		// only html format, exports are not possible atm 
		this.setTags(command, Resource.class, groupingEntity, null, null, null, null, MAX_TAGS, null);
		this.endTiming();
		return Views.POPULAR;
	}
	
	@Override
	protected int getFixedTagMax(final int tagMax) {
		return MAX_TAGS;
	}
	
	@Override
	public MultiResourceViewCommand instantiateCommand() {
		return new MultiResourceViewCommand();
	}

}
