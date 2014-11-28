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
package org.bibsonomy.wiki.tags.shared;

import java.util.Set;

import org.bibsonomy.util.Sets;
import org.bibsonomy.wiki.tags.SharedTag;

/**
 * Allows to include some predefined CSS styles. Wrapper needed for no direct inclusion
 * of a CSS file.
 * @author niebler
 */
public class DesignTag extends SharedTag {
	
	/** The tag name. */
	private final static String TAG_NAME = "design";
	/** The style attribute, which defines the used CSS file. */
	private final static String STYLE = "style";
	/** The list of allowed attributes for this tag. */
	private final static Set<String> ALLOWED_ATTRIBUTES_SET = Sets.asSet(STYLE);
	
	/**
	 * standard constructor.
	 */
	public DesignTag() {
		super(TAG_NAME);
	}

	/**
	 * checks, if attName is an allowed attribute.
	 * @param attName a string with a possible attribute name.
	 * @return true, if attName is an element of the set of allowed attributes.
	 */
	@Override
	public boolean isAllowedAttribute(final String attName) {
		return ALLOWED_ATTRIBUTES_SET.contains(attName);
	}

	/**
	 * Rendering Method. Creates a HTML string to be included in the Wiki.
	 */
	@Override
	protected String renderSharedTag() {
		// Loesche alles, was nach sonderbarer Navigation aussieht, aus dem Attributswert.
		final String style = this.getAttributes().get(STYLE).toLowerCase().replace("/", "").replace(".", "");
		return "<link rel=\"stylesheet\" href=\"/resources/css/cv_styles/" + style + ".css\" />";
	}

}
