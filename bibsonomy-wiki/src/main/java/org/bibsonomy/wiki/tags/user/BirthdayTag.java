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
 * This is a simple birthday-tag.
 * Usage: <birthday />
 * @author Bernd
 *
 */
public class BirthdayTag extends UserTag {
	
	private static final String TAG_NAME = "birthday";
	/*
	 * TODO: allow other formats as tag parameter (e.g. display without year or the english order mm/dd/yyyy..."
	 * TODO: maybe think about standardizing it with the rest of BibSonomy?
	 */
	private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
	
	/**
	 * set tag name
	 */
	public BirthdayTag() {
		super(TAG_NAME);
	}
	
	@Override
	protected String renderUserTag() {
		final Date birthday = this.requestedUser.getBirthday();
		// TODO: Insert something small indicating the user's birthday
		// if it coincides with the present day :)
		if (present(birthday)) {
			return "<div id='birthday'>" + this.renderString(fmt.print(birthday.getTime())) + "</div>";
		}
		return "";
	}

}
