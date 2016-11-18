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


import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bibsonomy.layout.csl.CSLStyle;
import org.bibsonomy.model.Layout;

/**
 * @author daill, lsc
 */
public class ExportPageCommand extends ResourceViewCommand {

	private Map<String, Layout> layoutMap;
	private Map<String, CSLStyle> cslLayoutMap;
	
	/**
	 * default constructor
	 */
	public ExportPageCommand() {
		this.layoutMap = new TreeMap<>();
		this.cslLayoutMap = new TreeMap<>();
	}

	/**
	 * @return layout map
	 */
	public Map<String, Layout> getLayoutMap() {
		return this.layoutMap;
	}
	
	/**
	 * adds all maps to the the layout map
	 * @param map
	 */
	public void addLayoutMap(final Map<String, ? extends Layout> map) {
		for (Entry<String, ? extends Layout> entry : map.entrySet()){
			this.layoutMap.put(entry.getValue().getDisplayName(), entry.getValue());
		}
		//this.layoutMap.putAll(map);
	}
	
	/**
	 * adds a layout to the layout map
	 * @param l 
	 */
	public void addLayout(Layout l) {
		this.layoutMap.put(l.getDisplayName(), l);
	}

	/**
	 * @return the cslLayoutMap
	 */
	public Map<String, CSLStyle> getCslLayoutMap() {
		return this.cslLayoutMap;
	}
	
	/**
	 * @param map
	 */
	public void setCslLayoutMap(final Map<String, CSLStyle> map) {
		this.cslLayoutMap = map;
	}	
}
