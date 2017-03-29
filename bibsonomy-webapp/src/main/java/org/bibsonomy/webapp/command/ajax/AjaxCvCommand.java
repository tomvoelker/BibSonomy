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
package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.wiki.enums.DefaultLayout;

/**
 * TODO: If we want dated versions of the wiki contents, we need to add
 * a date attribute here.
 * 
 * @author Bernd
 */
public class AjaxCvCommand extends AjaxCommand {
	
	/**
	 * default layout
	 */
	private DefaultLayout layout;
	
	/**
	 * renderOptions
	 */
	private String renderOptions;
	
	/**
	 * 
	 */
	private String wikiText;

	private String requestedGroup;	
	
	/**
	 * @return the layout
	 */
	public DefaultLayout getLayout() {
		return layout;
	}

	/**
	 * @param layout the layout to set
	 */
	public void setLayout(final DefaultLayout layout) {
		this.layout = layout;
	}

	/**
	 * @return the wikiText
	 */
	public String getWikiText() {
		return wikiText;
	}

	/**
	 * @param wikiText the wikiText to set
	 */
	public void setWikiText(final String wikiText) {
		this.wikiText = wikiText;
	}

	/**
	 * @return the renderOptions
	 */
	public String getRenderOptions() {
		return renderOptions;
	}

	/**
	 * @param renderOptions the renderOptions to set
	 */
	public void setRenderOptions(final String renderOptions) {
		this.renderOptions = renderOptions;
	}
	
	/**
	 * @return the requestedGroup
	 */
	public String getRequestedGroup() {
		return this.requestedGroup;
	}

	/**
	 * @param requestedGroup the requestedGroup to set
	 */
	public void setRequestedGroup(String requestedGroup) {
		this.requestedGroup = requestedGroup;
	}

}
