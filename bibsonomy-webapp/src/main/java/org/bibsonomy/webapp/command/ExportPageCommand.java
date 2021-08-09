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
package org.bibsonomy.webapp.command;


import java.util.Map;
import java.util.TreeMap;

import org.bibsonomy.layout.csl.CSLStyle;
import org.bibsonomy.model.Layout;

/**
 * @author daill, lsc
 */
public class ExportPageCommand extends ResourceViewCommand {

	private Map<String, Layout> layoutMap;
	private Map<String, CSLStyle> cslLayoutMap;
	private Map<String, CSLStyle> customCslLayoutMap;

	/**
	 * default constructor
	 */
	public ExportPageCommand() {
		this.layoutMap = new TreeMap<>();
		this.cslLayoutMap = new TreeMap<>();
		this.customCslLayoutMap = new TreeMap<>();
	}

	/**
	 * @return layout map
	 */
	public Map<String, Layout> getLayoutMap() {
		return this.layoutMap;
	}

	/**
	 * @return the cslLayoutMap
	 */
	public Map<String, CSLStyle> getCslLayoutMap() {
		return this.cslLayoutMap;
	}

	/**
	 * @return the customCslLayoutMap
	 */
	public Map<String, CSLStyle> getCustomCslLayoutMap() {
		return customCslLayoutMap;
	}
	
	/**
	 * @param map
	 */
	public void setCslLayoutMap(final Map<String, CSLStyle> map) {
		this.cslLayoutMap = map;
	}

	/**
	 * @param layoutMap the layoutMap to set
	 */
	public void setLayoutMap(final Map<String, Layout> layoutMap) {
		this.layoutMap = layoutMap;
	}

	/**
	 * @param customCslLayoutMap the customCslLayoutMap to set
	 */
	public void setCustomCslLayoutMap(final Map<String, CSLStyle> customCslLayoutMap) {
		this.customCslLayoutMap = customCslLayoutMap;
	}
}
