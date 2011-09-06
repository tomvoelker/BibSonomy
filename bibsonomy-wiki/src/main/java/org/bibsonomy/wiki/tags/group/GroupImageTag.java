package org.bibsonomy.wiki.tags.group;

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
		final StringBuffer renderedHTML = new StringBuffer();
		renderedHTML.append("<div class='groupImage'>");
		renderedHTML.append(this.renderImage(this.requestedGroup.getUsers().get(0).getName()));
		renderedHTML.append("<a href='/cv/"+this.requestedGroup.getUsers().get(0).getName()+"' style='text-align:center;'><div>" + this.requestedGroup.getUsers().get(0).getRealname() + "</div></a>");
		renderedHTML.append("</div>");

		return renderedHTML.toString();
	}

}
