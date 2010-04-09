package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Bean for Tag Sites
 * 
 * @author Michael Wagner
 * @version $Id$
 */
public class TagResourceViewCommand extends SimpleResourceViewCommand{
	
	/** tags to search for */
	private String requestedTags = "";
	
	/** tags to search for, as list */
	private List<String> requestedTagsList = null;
	
	/** the specified order */
	private String order = "added";
		
	/** bean for related tags */
	private RelatedTagCommand relatedTagCommand = new RelatedTagCommand();
	
	/** re-using relatedTagCommand to store similar tags */
	private RelatedTagCommand similarTags = new RelatedTagCommand();
	
	/** related users - needed for FolkRank */
	private RelatedUserCommand relatedUserCommand = new RelatedUserCommand();
	
	/**
	 * @return the requested tagstring as a list
	 */
	public List<String> getRequestedTagsList() {		
		// tagstring has not yet been tokenized 
		if (this.requestedTagsList == null) {
			this.requestedTagsList = new ArrayList<String>();			
			final StringTokenizer st = new StringTokenizer(requestedTags);
			while (st.hasMoreTokens()) {			
				final String tagname = st.nextToken();			
				this.requestedTagsList.add(tagname);			
			}			
		}		
		return this.requestedTagsList;
	}
	
	/**
	 * @return requested tags as string
	 */
	public String getRequestedTags() {
		return this.requestedTags;
	}	
	
	/**
	 * sets the requested tags
	 * @param requestedTags 
	 */
	public void setRequestedTags(final String requestedTags) {
		relatedTagCommand.setRequestedTags(requestedTags);
		this.requestedTags = requestedTags;
	}

	/**
	 * @return command with related tags
	 */
	public RelatedTagCommand getRelatedTagCommand() {
		return this.relatedTagCommand;
	}
	
	/**
	 * @return the relatedusercommand
	 */
	public RelatedUserCommand getRelatedUserCommand() {
		return this.relatedUserCommand;
	}
	
	/**
	 * @param relatedUserCommand
	 */
	public void setRelatedUserCommand(final RelatedUserCommand relatedUserCommand) {
		this.relatedUserCommand = relatedUserCommand;
	}

	/**
	 * @param relatedTagCommand command with related tags
	 */
	public void setRelatedTagCommand(final RelatedTagCommand relatedTagCommand) {
		this.relatedTagCommand = relatedTagCommand;
	}

	/**
	 * @return order
	 */
	public String getOrder() {
		return this.order;
	}

	/**
	 * @param order
	 */
	public void setOrder(final String order) {
		this.order = order;
	}

	/**
	 * @return the similarTags
	 */
	public RelatedTagCommand getSimilarTags() {
		return this.similarTags;
	}

	/**
	 * @param similarTags the similarTags to set
	 */
	public void setSimilarTags(final RelatedTagCommand similarTags) {
		this.similarTags = similarTags;
	}
	
}
