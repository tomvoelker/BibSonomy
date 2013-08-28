package org.bibsonomy.wiki.tags.group;

import org.bibsonomy.model.User;
import org.bibsonomy.wiki.tags.GroupTag;

/**
 * renders the group image of the group
 * @author tni
 */
public class GroupImageTag extends GroupTag {
	private static final String TAG_NAME = "groupimage";

	/**
	 * default constructor
	 */
	public GroupImageTag() {
		super(TAG_NAME);
	}

	private String renderImage(final String userName) {
		return "<img src='/picture/user/" + userName + "' />";
	}
	
	/*
	 * TODO: Rebuild this with the new group concept.
	 */
	@Override
	protected String renderGroupTag() {
		final StringBuilder renderedHTML = new StringBuilder();
		if (!this.requestedGroup.getUsers().isEmpty()) {
			final User user = this.requestedGroup.getUsers().get(0);
			renderedHTML.append("<div class='groupImage'>");
			renderedHTML.append(this.renderImage(user.getName()));
			renderedHTML.append("<a href='/cv/user/" + this.renderString(user.getName()) + "' style='text-align:center;'><div>" + this.renderString(user.getRealname()) + "</div></a>");
			renderedHTML.append("</div>");
		}

		return renderedHTML.toString();
	}

}
