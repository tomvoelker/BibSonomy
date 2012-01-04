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
import java.util.Map;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class PicaRecord {
	private final Map<String, LinkedList<Row>> rows = new HashMap<String, LinkedList<Row>>();
	
	/**
	 * Adds a row to this object
	 * 
	 * @param row
	 */
	public void addRow(final Row row){
		final String category = row.getCat();
		if (!isExisting(category)){
			this.rows.put(category, new LinkedList<Row>());
		}
		this.rows.get(category).add(row);
	}

	/**
	 * tests if the given pica category is existing
	 * 
	 * @param cat
	 * @return boolean
	 */
	public boolean isExisting(final String cat) {
		return this.rows.containsKey(cat);
	}
	
	/**
	 * get a specific row by category
	 * 
	 * @param cat
	 * @return Row
	 */
	public Row getRow(final String cat){
		if (isExisting(cat)){
			final LinkedList<Row> list = rows.get(cat);
		
			if (list.size() > 0) {
				return list.get(0);
			} 
		}
		return null;
	}
	
	/**
	 * @param cat
	 * @return The row for the given category.
	 */
	public LinkedList<Row> getRows(final String cat){
		return this.rows.get(cat);
	}
	
}
