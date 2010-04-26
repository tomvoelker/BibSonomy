package org.bibsonomy.webapp.controller.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.webapp.command.reporting.GroupReportingCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import resources.Bibtex;

/**
 * Controller for group reporting pages.
 * 
 * FIXME: This is just a starting point.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class GroupReportingPageController implements MinimalisticController<GroupReportingCommand> {

	private static final Log log = LogFactory.getLog(GroupReportingPageController.class);
	
	/** logic interface */
	private LogicInterface logic;
	

	@Override
	public View workOn(GroupReportingCommand command) {
		
		// allow only logged-in users FIXME: check errormsg
		if (command.getContext().getLoginUser().getName() == null) {
			log.error("Not logged in on reporting page!");
			throw new MalformedURLSchemeException("Not logged in!");
		}
		
		// if no group given -> error FIXME: check errormsg
		if (command.getRequestedGroup() == null) {
			log.error("Invalid query /group without roup name");
			throw new MalformedURLSchemeException("error.group_page_without_groupname");
		}
		
		// if no tags given return FIXME: check errormsg
		if (command.getRequestedTags() == null || command.getRequestedTags().length() == 0) {
			log.error("Invalid query /tag without tag");
			throw new MalformedURLSchemeException("error.tag_page_without_tag");
		}		
		
		/*
		 * assemble taglist
		 */
		List<String> tags = new ArrayList<String>();
		tags.add(command.getRequestedTags());
		
		/*
		 * fetch all bibtex & remove duplicates
		 */
		List<Post<BibTex>> groupBibtexEntries = logic.getPosts(BibTex.class, GroupingEntity.GROUP, command.getRequestedGroup(), tags, null, null, null, 0, 10000, null);
		BibTexUtils.removeDuplicates(groupBibtexEntries);
		
		
		/*
		 * sort entries in descending order by year
		 */
		BibTexUtils.sortBibTexList(groupBibtexEntries, SortUtils.parseSortKeys("year"), SortUtils.parseSortOrders("desc"));
		
		
		/*
		 * init entrytypes 
		 */
		for (String type : Bibtex.entrytypes) { command.getPublicationCounts().getColumnHeaders().add(type); }
		/*
		 * loop over entries, accumulate and fill command
		 */
		BibTex bib;
		int lastYear = Integer.MIN_VALUE;
		HashMap<String,Integer> row = null;
		for (Post<BibTex> post : groupBibtexEntries) {
			bib = post.getResource();
			try {
				int curYear = Integer.valueOf(bib.getYear());
				if ( curYear != lastYear) {
					if (lastYear != Integer.MIN_VALUE) {
						// write last row into command, if there is one
						command.getPublicationCounts().getValues().put(lastYear, row);
						command.getPublicationCounts().getRowHeaders().add(lastYear);
					}
					// init a new row with zero values
					row = new HashMap<String,Integer>();
					for (String type : Bibtex.entrytypes) {
						row.put(type, 0);
					}
				}
				// increment counter of type TYPE in current year
				this.increment(row, bib.getEntrytype());
				lastYear = curYear;
				
			} catch (NumberFormatException ex) {
				// ignore silently
			}
			
		}
		// add the last year
		command.getPublicationCounts().getValues().put(lastYear, row);
		command.getPublicationCounts().getRowHeaders().add(lastYear);		
		
		/*
		 *  create some dummy data for testing 
		 *  FIXME: replace this by fetching the data from the logic instead!
		 */		
//		final int[] dummyYears = {2009,2008,2007,2006,2005};
//		final String[] dummyTypes = Bibtex.entrytypes;
//		int dummyValue = 23;
//		
//		// initialize column + row headings
//		for (String type : dummyTypes) { command.getPublicationCounts().getColumnHeaders().add(type); }
//		for (Integer year : dummyYears) { command.getPublicationCounts().getRowHeaders().add(year); }		
//		
//		// init values
//		HashMap<String,Integer> row2;
//		for (int year : dummyYears) {
//			row2 = new HashMap<String,Integer>();			
//			// write row values
//			for (String type : dummyTypes) {
//				row2.put(type, dummyValue++);
//			}			
//			// store row in command
//			command.getPublicationCounts().getValues().put(year, row);
//		}
						
		
		// TODO: add totals: sum up values for each years 
		
		return Views.REPORTING;
	}
	
	
	public LogicInterface getLogic() {
		return this.logic;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
	
	@Override
	public GroupReportingCommand instantiateCommand() {
		return new GroupReportingCommand();
	}
	
	/*
	 * increment map at position 'key'
	 */
	private void increment(Map<String, Integer> map, String key) {
		if (key == null || !map.containsKey(key.toLowerCase())) {
			return;
		}
		int lastVal = map.get(key);
		map.put(key, lastVal + 1);
	}

}
