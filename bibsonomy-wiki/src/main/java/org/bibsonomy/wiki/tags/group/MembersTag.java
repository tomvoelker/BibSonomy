package org.bibsonomy.wiki.tags.group;

import org.bibsonomy.model.User;
import org.bibsonomy.wiki.tags.GroupTag;

/**
 * renders all members of the group
 * (image and name)
 * @author tni
 */
public class MembersTag extends GroupTag {
	private static final String TAG_NAME = "members";

	/**
	 * default constructor
	 */
	public MembersTag() {
		super(TAG_NAME);
	}

	private String renderImage(final String userName) {
		return "<img height='100px' src='/picture/user/" + this.renderString(userName) + "' />";
	}

	/**
	 * creates a list of pictures of all members of this group (except for the group owner itself) as well as their names.
	 * The HTML div container is of the class imageContainer.
	 * TODO: Fix this with the new group concept.
	 */
	@Override
	protected String renderGroupTag() {
		final StringBuilder renderedHTML = new StringBuilder();
		for (final User user : this.requestedGroup.getUsers()) {
			if (!user.getName().equals(this.requestedGroup.getName())) {
				renderedHTML.append("<div class='imageContainer'>");
				renderedHTML.append(this.renderImage(user.getName()));
				renderedHTML.append("<p style='text-align:center;'><a href=\"/cv/user/" + user.getName() + "\">" + this.renderString(user.getRealname()) + "</a></p>");
				renderedHTML.append("</div>");
			}
		}
		return renderedHTML.toString();
	}
}
