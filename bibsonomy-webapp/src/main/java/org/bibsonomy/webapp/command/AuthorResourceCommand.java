package org.bibsonomy.webapp.command;

/**
 * @author daill
 * @version $Id$
 */
public class AuthorResourceCommand extends TagResourceViewCommand {
	
	// the requested Author
	private String requestedAuthor = "";
		
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
}
