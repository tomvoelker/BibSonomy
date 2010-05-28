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

	/** transposes displayed matrix if is set to 1 */
	private String transposeMatrix = "";

	private String requestedTags;

	/**
	 * @return the requestedGroup
	 */
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup the requestedGroup to set
	 */
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

	/**
	 * @return the transposeMatrix
	 */
	public String getTransposeMatrix() {
		return this.transposeMatrix;
	}

	/**
	 * @param transposeMatrix the transposeMatrix to set
	 */
	public void setTransposeMatrix(String transposeMatrix) {
		this.transposeMatrix = transposeMatrix;
	}

	/**
	 * @return the requestedTags
	 */
	public String getRequestedTags() {
		return this.requestedTags;
	}

	/**
	 * @param requestedTags the requestedTags to set
	 */
	public void setRequestedTags(String requestedTags) {
		this.requestedTags = requestedTags;
	}

	/**
	 * @return the publicationCounts
	 */
	public ReportingTableCommand<Integer, String, Integer> getPublicationCounts() {
		return this.publicationCounts;
	}
	
}
