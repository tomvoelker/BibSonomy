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
package org.bibsonomy.wiki.enums;

/**
 * Enum to summarize the default layouts (i.e. wiki texts) available for the CV
 * wiki. These map to files in the wiki submodule. the encoded strings represent
 * the file prefix for a layout.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @author Bernd Terbrack
 * @author Thomas Niebler
 */
public enum DefaultLayout {

	/** default user layout 2 (English) */
	LAYOUT_DEFAULT_II_EN("user2en"),

	/** default user layout 2 (German) */
	LAYOUT_DEFAULT_II_GER("user2de"),

	/** default user layout 2 (Russian) */
	LAYOUT_DEFAULT_II_RU("user2ru"),

	/** default user layout 1 (English) */
	LAYOUT_DEFAULT_I_EN("user1en"),

	/** default user layout 1 (Russian) */
	LAYOUT_DEFAULT_I_RU("user1ru"),

	/** default user layout 2 (German) */
	LAYOUT_DEFAULT_I_GER("user1de"),

	/** current user layout (i.e. the one ) */
	LAYOUT_CURRENT(""),

	/** default group layout 1 (English) */
	LAYOUT_G_DEFAULT_I_EN("group1en"),

	/** default group layout 1 (German) */
	LAYOUT_G_DEFAULT_I_GER("group1de");

	/** the message key which holds the default cv wiki text */
	private final String ref;

	/**
	 * Constructor
	 * 
	 * @param ref
	 *            - the message key
	 */
	private DefaultLayout(final String ref) {
		this.ref = ref;
	}

	/**
	 * Get the message key for the current default layout
	 * 
	 * @return the message key for the given default cv wiki
	 */
	public String getRef() {
		return this.ref;
	}
}
