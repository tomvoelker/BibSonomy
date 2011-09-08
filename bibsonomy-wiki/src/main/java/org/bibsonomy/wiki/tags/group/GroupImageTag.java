package org.bibsonomy.wiki.tags.group;

import org.bibsonomy.model.User;
import org.bibsonomy.wiki.tags.GroupTag;

public class GroupImageTag extends GroupTag {
	private static final String TAG_NAME = "groupimage";

	public GroupImageTag() {
		super(TAG_NAME);
	}

	private String renderImage(final String userName) {
		return "<img src='/picture/user/" + userName + "' />";
	}

	@Override
	protected String renderGroupTag() {
		final StringBuilder renderedHTML = new StringBuilder();
		if (!this.requestedGroup.getUsers().isEmpty()) {
			final User user = this.requestedGroup.getUsers().get(0);
			renderedHTML.append("<div class='groupImage'>");
			renderedHTML.append(this.renderImage(user.getName()));
			renderedHTML.append("<a href='/cv/" + user.getName() + "' style='text-align:center;'><div>" + user.getRealname() + "</div></a>");
			renderedHTML.append("</div>");
		}

		return renderedHTML.toString();
	}

}
