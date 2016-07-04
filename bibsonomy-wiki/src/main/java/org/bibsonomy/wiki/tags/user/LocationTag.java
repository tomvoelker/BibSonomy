/**
 * BibSonomy CV Wiki - Wiki for user and group CVs
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.wiki.tags.user;

import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.wiki.tags.UserTag;

/**
 * This is a simple location-tag.
 * Usage: <location />
 * @author Bernd
 *
 */
public class LocationTag extends UserTag{
	private static final String TAG_NAME = "location";
	
	/**
	 * set name of the tag
	 */
	public LocationTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected String renderUserTag() {
		final String location = this.renderString(this.requestedUser.getPlace());
		return ValidationUtils.present(location) ? "<div id='location'>" + location + "</div>" : "";
	}
}
