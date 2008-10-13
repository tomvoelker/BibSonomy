package org.bibsonomy.webapp.command;

/**
 * @author fba
 * @version $Id$
 */
public class PostCommand extends SimpleResourceViewCommand {
	private TagCloudCommand tagcloud = new TagCloudCommand();
	private String requestedUser;

	public TagCloudCommand getTagcloud() {
		return this.tagcloud;
	}

	public void setTagcloud(TagCloudCommand tagcloud) {
		this.tagcloud = tagcloud;
	}

	public String getRequestedUser() {
		return this.requestedUser;
	}

	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}
}
