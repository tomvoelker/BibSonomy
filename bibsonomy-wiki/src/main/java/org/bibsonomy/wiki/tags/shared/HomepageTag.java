/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.wiki.tags.shared;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.wiki.tags.UserTag;

/**
 * This is a simple homepage-tag.
 * Usage: <homepage />
 * @author janus
 */
public class HomepageTag extends UserTag {
	private static final String TAG_NAME = "homepage";
	
	/**
	 * set the name of the tag
	 */
	public HomepageTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected String renderUserTag() {
		final String homepage = this.requestedUser.getHomepage() == null ? "" : this.renderString(this.requestedUser.getHomepage().toString());
		// TODO: add attribute to set the displayed link name?
		return present(homepage) ? "<div id='homepage'><a href=\"" + homepage + "\" rel=\"nofollow me\">" + homepage + "</a></div>" : "";
	}

}
