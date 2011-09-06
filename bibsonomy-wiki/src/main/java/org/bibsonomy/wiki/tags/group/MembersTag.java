package org.bibsonomy.wiki.tags.group;

import org.bibsonomy.model.User;
import org.bibsonomy.wiki.tags.AbstractTag;

public class MembersTag extends AbstractTag {
	private static final String TAG_NAME = "members";

	public MembersTag() {
		super(TAG_NAME);
	}

	@Override
	protected String render() {
		// render the member tag if its a group request, else screw it!
		return this.requestedGroup != null  ? this.renderMembersTag() : "not available";
	}

	private String renderMembersTag() {
		final StringBuffer renderedHTML = new StringBuffer();
		for (final User user : this.requestedGroup.getUsers()) {
			if (!user.getName().equals(this.requestedGroup.getName())) {
				renderedHTML.append("<div class='imageContainer'>");
				renderedHTML.append(this.renderImage(user.getName()));
				renderedHTML.append("<p style='text-align:center;'>" + user.getRealname() + "</p>");
				renderedHTML.append("</div>");
			}
		}
		return renderedHTML.toString();
	}

	private String renderImage(final String userName) {
		return "<img height='100px' src='/picture/user/" + userName + "' />";
	}
}
