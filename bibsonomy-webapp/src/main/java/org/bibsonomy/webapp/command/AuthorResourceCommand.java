package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author daill
 * @version $Id$
 */
public class AuthorResourceCommand extends ResourceViewCommand {
	
	// the requested Author
	private String requestedAuthor = "";
	
	// the requested tags
	private String requestedTags = "";
	
	// the bean for the related tags
	private RelatedTagCommand relatedTagCommand = new RelatedTagCommand();

	/**
	 * @return string with the requested author
	 */
	public String getRequestedAuthor() {
		return this.requestedAuthor;
	}

	/**
	 * @param requestedAuthor
	 */
	public void setRequestedAuthor(String requestedAuthor) {
		this.requestedAuthor = requestedAuthor;
	}

	/**
	 * @return string of requested tags
	 */
	public String getRequestedTags() {
		return this.requestedTags;
	}

	/**
	 * @param requestedTags
	 */
	public void setRequestedTags(String requestedTags) {
		relatedTagCommand.setRequestedTags(requestedTags);
		this.requestedTags = requestedTags;
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
	 * @return RelatedTagCommand with the related teags
	 */
	public RelatedTagCommand getRelatedTagCommand() {
		return this.relatedTagCommand;
	}

	/**
	 * @param relatedTagCommand
	 */
	public void setRelatedTagCommand(RelatedTagCommand relatedTagCommand) {
		this.relatedTagCommand = relatedTagCommand;
	}
}
