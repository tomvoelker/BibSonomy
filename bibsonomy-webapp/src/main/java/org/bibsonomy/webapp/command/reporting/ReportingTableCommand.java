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
package org.bibsonomy.webapp.command.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract command which holds reporting information in row / column format.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @param <T> - the type of the row headers
 * @param <U> - the type of the column headers
 * @param <V> - the type of the values
 */
public class ReportingTableCommand<T,U,V> {
	
	/** data matrix */
	private Map<T, Map<U, V>> values = new HashMap<T, Map<U,V>>();

	/** labels of the rows of the matrix*/
	private List<T> rowHeaders = new ArrayList<T>();
	
	/** labels of the columns of the matrix */
	private List<U> columnHeaders = new ArrayList<U>();

	/**
	 * @return the values
	 */
	public Map<T, Map<U, V>> getValues() {
		return this.values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(Map<T, Map<U, V>> values) {
		this.values = values;
	}

	/**
	 * @return the rowHeaders
	 */
	public List<T> getRowHeaders() {
		return this.rowHeaders;
	}

	/**
	 * @param rowHeaders the rowHeaders to set
	 */
	public void setRowHeaders(List<T> rowHeaders) {
		this.rowHeaders = rowHeaders;
	}

	/**
	 * @return the columnHeaders
	 */
	public List<U> getColumnHeaders() {
		return this.columnHeaders;
	}

	/**
	 * @param columnHeaders the columnHeaders to set
	 */
	public void setColumnHeaders(List<U> columnHeaders) {
		this.columnHeaders = columnHeaders;
	}

}
