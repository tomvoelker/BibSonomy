package org.bibsonomy.webapp.command.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract command which holds reporting information in row / column format.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
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
