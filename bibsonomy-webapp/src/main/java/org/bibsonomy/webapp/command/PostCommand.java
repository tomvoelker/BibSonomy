package org.bibsonomy.webapp.command;

/**
 * @author fba
 * @version $Id$
 */
public class PostCommand extends ResourceViewCommand {
	private TagCloudCommand tagcloud = new TagCloudCommand();

	public TagCloudCommand getTagcloud() {
		return this.tagcloud;
	}

	public void setTagcloud(TagCloudCommand tagcloud) {
		this.tagcloud = tagcloud;
	}

}
