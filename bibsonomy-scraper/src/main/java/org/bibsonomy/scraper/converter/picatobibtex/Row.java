/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.scraper.converter.picatobibtex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class Row {
	private final String category;
	private final Map<String, List<String>> subfields = new HashMap<String, List<String>>();
	
	
	/**
	 * @param category
	 */
	public Row(final String category){
		this.category = category;
	}

	/**
	 * Adds a subfield to the row object
	 * 
	 * @param subCategory
	 * @param content
	 */
	public void addSubField(final String subCategory, final String content) {
		if (!this.subfields.containsKey(subCategory)) {
			this.subfields.put(subCategory, new LinkedList<String>());
		}
		this.subfields.get(subCategory).add(content);
	}
	
	/**
	 * @param subfields
	 */
	public void addSubFields(final Map<String, List<String>> subfields) {
		final Set<Entry<String, List<String>>> entrySet = subfields.entrySet();
		for (final Entry<String, List<String>> entry : entrySet) {
			final String key = entry.getKey();
			final List<String> values = entry.getValue();
			if (this.subfields.containsKey(key)) {
				this.subfields.get(key).addAll(values);
			} else {
				this.subfields.put(key, values);
			}
		}
	}

	/**
	 * Returns the category of the row
	 * 
	 * @return String
	 */
	public String getCategory() {
		return this.category;
	}

	/**
	 * Tests if the given subfield is existing in this row
	 * 
	 * @param sub
	 * @return boolean
	 */
	public boolean isExisting(final String sub) {
		return this.subfields.containsKey(sub);
	}
	
	/**
	 * Returns the requested SubField
	 * 
	 * @param subCategory
	 * @return The content of this subCategory
	 */
	public List<String> getSubField(final String subCategory){
		return this.subfields.get(subCategory);
	}
	
	
	@Override
	public String toString() {
		return this.category + ": " + this.subfields;
	}

	/**
	 * @return The subfields
	 */
	public Map<String, List<String>> getSubfields() {
		return this.subfields;
	}
}
