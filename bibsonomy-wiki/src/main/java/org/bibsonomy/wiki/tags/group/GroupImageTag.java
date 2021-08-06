/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.wiki.tags.group;

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
		// TODO: use urlgenerator
		return "<img class='user-avatar img-responsive group-cv-image' title='" + userName + "' src='/picture/user/" + userName + "' />";
	}
	
	/*
	 * TODO: Rebuild this with the new group concept.
	 */
	@Override
	protected String renderGroupTag() {
		final StringBuilder renderedHTML = new StringBuilder();

		renderedHTML.append("<div class=\"imageContainer\">");
		final String groupName = this.requestedGroup.getName();
		renderedHTML.append("<a href='/cv/user/" + this.renderString(groupName) + "' title='" + this.renderString(groupName) + "' class='img-thumbnail img-responsive'>");
		renderedHTML.append(this.renderImage(groupName));
		renderedHTML.append("<span class='group-cv-image-name'>" + this.renderString(this.requestedGroup.getRealname()) + "</span>");
		renderedHTML.append("</a></div>");

		return renderedHTML.toString();
	}

}
