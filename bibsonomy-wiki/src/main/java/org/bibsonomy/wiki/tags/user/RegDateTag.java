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

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;

import org.bibsonomy.wiki.tags.UserTag;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This is a simple registered-date-tag Usage: <regdate />
 * 
 * @author Bernd Terbrack
 */
public class RegDateTag extends UserTag {
	private static final String TAG_NAME = "regdate";

	/*
	 * TODO Unify date handling for this tag and for the BirthdayTag (same options) probably useful would be an AbstractDate Tag
	 */
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

	/**
	 * set the tag name
	 */
	public RegDateTag() {
		super(TAG_NAME);
	}

	@Override
	protected String renderUserTag() {
		final Date regDate = this.requestedUser.getRegistrationDate();
		if (present(regDate)) {
			return "<div id='regDate'>" + this.renderString(fmt.print(regDate.getTime())) + "</div>";
		}
		return "";
	}
}