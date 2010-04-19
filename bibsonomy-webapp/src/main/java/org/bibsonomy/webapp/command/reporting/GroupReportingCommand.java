package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Reporting command for a group.
 * 
 * FIXME: This is just a starting point.
 * 
 * @author dbenz
 * @version $Id$
 */
public class GroupReportingCommand extends BaseCommand {

	/** holds the table with the publication counts per type */ 
	private final ReportingTableCommand<Integer, String, Integer> publicationCounts = new ReportingTableCommand<Integer, String, Integer>();
	
	/** the name of the requested group */
	private String requestedGroup = "";

	/** how to show reporting matrix. empty or normal shows years in rows and transpose shows years in columns */
	private String transposeMatrix = "";

	public String getRequestedGroup() {
		return this.requestedGroup;
	}
	
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	public ReportingTableCommand<Integer, String, Integer> getPublicationCounts() {
		return this.publicationCounts;
	}

	public void setTransposeMatrix(String transposeMatrix) {
		this.transposeMatrix = transposeMatrix;
	}

	public String getTransposeMatrix() {
		return transposeMatrix;
	}

	
	
}
