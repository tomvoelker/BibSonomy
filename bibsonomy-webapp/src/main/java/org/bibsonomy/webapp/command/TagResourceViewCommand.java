package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.bibsonomy.model.logic.Order;

/**
 * Bean for Tag Sites
 * 
 * @author Michael Wagner
 * @version $Id$
 */
public class TagResourceViewCommand extends ResourceViewCommand{
	
	/** tags to search for */
	private String requestedTags = "";
	
	/** the specified order */
	private Order requestedOrder;
		
	/** bean for related tags */
	private RelatedTagCommand relatedTagCommand = new RelatedTagCommand();
	private RelatedUserCommand relatedUserCommand = new RelatedUserCommand();
	
	/**
	 * @return the requested tagstring as a list
	 */
	public List<String> getRequestedTagsList() {
		List<String> tags = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(requestedTags);
		while (st.hasMoreTokens()) {			
			String tagname = st.nextToken();			
			tags.add(tagname);			
		}
		
		return tags;
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
	public void setRequestedTags(String requestedTags) {
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
	public void setRelatedUserCommand(RelatedUserCommand relatedUserCommand) {
		this.relatedUserCommand = relatedUserCommand;
	}

	/**
	 * @param relatedTagCommand command with related tags
	 */
	public void setRelatedTagCommand(RelatedTagCommand relatedTagCommand) {
		this.relatedTagCommand = relatedTagCommand;
	}

	/**
	 * @return order
	 */
	public Order getRequestedOrder() {
		return this.requestedOrder;
	}

	/**
	 * @param requestedOrder
	 */
	public void setRequestedOrder(final Order requestedOrder) {
		this.requestedOrder = requestedOrder;
	}
	
}
