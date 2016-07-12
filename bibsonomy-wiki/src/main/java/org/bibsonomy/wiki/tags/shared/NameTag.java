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
package org.bibsonomy.wiki.tags.shared;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Set;

import org.bibsonomy.util.Sets;
import org.bibsonomy.wiki.tags.SharedTag;

/**
 * This is a simple name-tag.
 * Usage: <name />
 * 
 * @author Bernd Terbrack
 */
public class NameTag extends SharedTag {
	
	private final static String PLAIN = "plain";
	private final static Set<String> ALLOWED_ATTRIBUTES_SET = Sets.asSet(PLAIN);
	
	/*
	 * TODO: DISCUSS: should we use the homepage link for the real name?
	 * would it not be better to have a homepage tag and have the name link to the bibsonomy-page of the user?
	 */
	private static final String TAG_NAME = "name";
	
	/**
	 * default constructor
	 */
	public NameTag() {
		super(TAG_NAME);
	}

	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}
	
	@Override
	protected String renderSharedTag() {
		final String name = this.getRequestedRealName();
		
		if (present(name)) {
			// Vielleicht hier noch einen Link zum CV des anderen Users rein? Oder
			// zur persoenlichen Homepage?
			return (this.getAttributes().get(PLAIN) != null ? "" : "<span id='name'><a href='/user/"
					+ this.renderString(this.getRequestedName()) + "'>")
					+ this.renderString(name)
					+ (this.getAttributes().get(PLAIN) != null ? "" : "</a></span>");
		}
		return "";
	}

}
