/**
 * BibSonomy-Layout - Layout engine for the webapp.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.standard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;

/**
 * Holds and manages the available standard layouts.
 * 
 * @author:  lsc
 */
public class StandardLayouts {
	private static final Log log = LogFactory.getLog(StandardLayouts.class);

	/**
	 * Can be configured by the setter: the path where the default layout files are.
	 */
	private final String defaultLayoutFilePath = "org/bibsonomy/layout/standard";
	/**
	 * saves all loaded layouts (bibtex, html, burst, ...)
	 */
	private Map<String, StandardLayout> layouts;
	
	
	
	/**
	 * Initialize the layouts by loading them into a map.
	 * @throws IOException 
	 */
	public StandardLayouts() throws IOException {
		loadDefaultLayouts();
	}

	/**
	 * Loads default filters into a map.
	 * 
	 * @throws IOException 
	 */
	private void loadDefaultLayouts() throws IOException {
		/*
		 * create a new hashmap to store the layouts
		 */
		layouts = new TreeMap<>();
		/*
		 * load layout definition from XML file
		 */
		final List<StandardLayout> standardLayouts = new XMLLayoutReader(new BufferedReader(new InputStreamReader(LayoutUtils.getResourceAsStream(defaultLayoutFilePath + "/" + "StandardLayouts.xml"), StringUtils.CHARSET_UTF_8))).getLayoutsDefinitions();
		log.info("found " + standardLayouts.size() + " layout definitions");
		/*
		 * iterate over all layout definitions
		 */
		for (final StandardLayout standardLayout : standardLayouts) {
			log.debug("loading layout " + standardLayout.getName());
			layouts.put(standardLayout.getName(), standardLayout);
		}
		log.info("loaded " + layouts.size() + " layouts");
	}

	/**
	 * @param layout
	 * @return the requested layout. This is for layouts which don't have item
	 *         parts for specific publication types. 
	 */
	public StandardLayout getLayout(final String layout) {
		return layouts.get(layout);
	}

	/** Removes all filters from the cache and loads the default filters.
	 * @throws IOException
	 */
	protected void resetFilters() throws IOException {
		loadDefaultLayouts();
	}

	@Override
	public String toString() {
		return layouts.toString();
	}
	
	/**
	 * Returns a map with all layouts
	 * 
	 * @return Map with all Layouts
	 */
	public Map<String, StandardLayout> getLayoutMap(){
		return this.layouts;
	}
}