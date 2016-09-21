/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command;

import org.bibsonomy.model.User;

/**
 * @author philipp
 */
public class CvPageViewCommand extends ResourceViewCommand {
	private String wikiText;
	private String renderedWikiText;

	private String requestedType;

	private boolean isGroup = false;
	private User user;

	/**
	 * @return the wikiText
	 */
	public String getWikiText() {
		return this.wikiText.trim();
	}

	/**
	 * @param wikiText the wikiText to set
	 */
	public void setWikiText(final String wikiText) {
		this.wikiText = wikiText.trim();
	}

	/**
	 * @return the renderedWikiText
	 */
	public String getRenderedWikiText() {
		return this.renderedWikiText;
	}

	/**
	 * @param renderedWikiText the renderedWikiText to set
	 */
	public void setRenderedWikiText(final String renderedWikiText) {
		this.renderedWikiText = renderedWikiText;
	}

	/**
	 * @return the isGroup
	 */
	public boolean getIsGroup() {
		return this.isGroup;
	}

	/**
	 * @param isGroup the isGroup to set
	 */
	public void setIsGroup(final boolean isGroup) {
		this.isGroup = isGroup;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return this.user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(final User user) {
		this.user = user;
	}

	/**
	 * @return the requestedType
	 */
	public String getRequestedType() {
		return this.requestedType;
	}

	/**
	 * @param requestedType
	 *            the requestedType to set
	 */
	public void setRequestedType(final String requestedType) {
		this.requestedType = requestedType;
	}

}
