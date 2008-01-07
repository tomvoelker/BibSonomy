package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Bean for Group-Sites
 *
 * @author  Stefan Stuetzer
 * @version $Id$
 */
public class GroupResourceViewCommand extends ResourceViewCommand {

	/** the group whode resources are requested*/
	private String requestedGroup = "";
	
	/** tags to search for */
	private String requestedTags = "";
		
	/** bean for related tags */
	private RelatedTagCommand relatedTagCommand = new RelatedTagCommand();
	
	/** bean for group members */
	private GroupMemberCommand memberCommand = new GroupMemberCommand();
	
	/**
	 * @return requestedGroup name of the group whose resources are requested
	 */
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 *  @param requestedGroup name of the group whose resources are requested
	 */
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}
	
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
	 * @param relatedTagCommand command with related tags
	 */
	public void setRelatedTagCommand(RelatedTagCommand relatedTagCommand) {
		this.relatedTagCommand = relatedTagCommand;
	}

	/**
	 *  @return command with all group members (in dependence of the group privacy level)
	 */
	public GroupMemberCommand getMemberCommand() {
		return this.memberCommand;
	}

	/**
	 * @param memberCommand command with group members
	 */
	public void setMemberCommand(GroupMemberCommand memberCommand) {
		this.memberCommand = memberCommand;
	}	
}