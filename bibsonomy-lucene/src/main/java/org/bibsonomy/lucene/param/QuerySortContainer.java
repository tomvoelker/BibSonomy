/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.lucene.param;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

/**
 * Container for a query string and a filter string for lucene 
 * 
 * if we can use more than one filter, we should use an arrayList or something
 * like this for filter. with methods addFilter, setFilter, getFilter
 * 
 * @version $Id$
 *
 */

public class QuerySortContainer {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5889003240930421319L;
	
	
	/**
	 * query
	 */
	private Query query;

	/**
	 * filter
	 */
	private Sort sort;

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return this.query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
		
	}
	
	/**
	 * @return the query
	 */
	public Sort getSort() {
		return this.sort;
	}

	/**
	 * @param query the query to set
	 */
	public void setSort(Sort sort) {
		this.sort = sort;
		
	}
	
}