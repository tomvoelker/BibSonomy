/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * General command for ajax requests
 * 
 * @author fba
 */
@Getter
@Setter
public class GeneralAjaxCommand extends AjaxCommand<String> {
	/**
	 * page title
	 */
	private String pageTitle; 	
	/**
	 * page URL
	 */
	private String pageURL;	
	/**
	 * page description
	 */
	private String pageDescription;	
	/**
	 * page keywords
	 */
	private String pageKeywords;
	/**
	 * generic query parameter
	 */
	private String q;
	
	/**
	 * generic user name parameter
	 */
	private String requestedUser;
	
	/**
	 * a list of bibtexs 
	 */
	private List<Post<BibTex>> bibtexPosts;

	/**
	 * @return the pageTitle
	 */
	@Override
	public String getPageTitle() {
		return this.pageTitle;
	}

	/**
	 * @param pageTitle the pageTitle to set
	 */
	@Override
	public void setPageTitle(final String pageTitle) {
		this.pageTitle = pageTitle;
	}

}
