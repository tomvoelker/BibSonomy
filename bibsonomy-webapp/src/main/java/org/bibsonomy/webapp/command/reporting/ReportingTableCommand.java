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
	

	
	public Map<T, Map<U, V>> getValues() {
		return this.values;
	}

	public void setValues(Map<T, Map<U, V>> values) {
		this.values = values;
	}

	public List<T> getRowHeaders() {
		return this.rowHeaders;
	}

	public void setRowHeaders(List<T> rows) {
		this.rowHeaders = rows;
	}

	public List<U> getColumnHeaders() {
		return this.columnHeaders;
	}

	public void setColumnHeaders(List<U> columns) {
		this.columnHeaders = columns;
	}

}
