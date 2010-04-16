package org.bibsonomy.webapp.controller.reporting;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.reporting.GroupReportingCommand;
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
		
		/*
		 *  create some dummy data for testing 
		 *  FIXME: replace this by fetching the data from the logic instead!
		 */		
		final int[] dummyYears = {2009,2008,2007,2006,2005};
		final String[] dummyTypes = Bibtex.entrytypes;
		int dummyValue = 23;
		
		// initialize column + row headings
		for (String type : dummyTypes) { command.getPublicationCounts().getColumnHeaders().add(type); }
		for (Integer year : dummyYears) { command.getPublicationCounts().getRowHeaders().add(year); }		
		
		// init values
		HashMap<String,Integer> row;
		for (int year : dummyYears) {
			row = new HashMap<String,Integer>();			
			// write row values
			for (String type : dummyTypes) {
				row.put(type, dummyValue++);
			}			
			// store row in command
			command.getPublicationCounts().getValues().put(year, row);
		}
						
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

}
