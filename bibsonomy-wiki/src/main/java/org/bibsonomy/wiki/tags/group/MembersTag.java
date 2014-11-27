/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
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
