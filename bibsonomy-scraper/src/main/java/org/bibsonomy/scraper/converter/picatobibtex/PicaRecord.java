package org.bibsonomy.scraper.converter.picatobibtex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author C. Kramer
 * @version $Id$
 */
public class PicaRecord {
	private Map<String, LinkedList<Row>> rows = new HashMap<String, LinkedList<Row>>();
	
	/**
	 * Adds a row to this object
	 * 
	 * @param row
	 */
	public void addRow(Row row){
		LinkedList<Row> list = null;
		
		if(rows.containsKey(row.getCat())){
			list = rows.get(row.getCat());
			list.add(row);
		} else {
			list = new LinkedList<Row>();
			list.add(row);
			this.rows.put(row.getCat(), list);
		}
	}

	/**
	 * tests if the given pica category is existing
	 * 
	 * @param cat
	 * @return boolean
	 */
	public boolean isExisting(String cat){
		return rows.containsKey(cat);
	}
	
	/**
	 * get a specific row by category
	 * 
	 * @param cat
	 * @return Row
	 */
	public Row getRow(String cat){
		if (isExisting(cat)){
			LinkedList<Row> list = rows.get(cat);
		
			if(list.size() > 0){
				return list.get(0);
			} else {
				return null;
			}
		}
		
		return null;
	}
	
	/**
	 * @param cat
	 * @return The row for the given category.
	 */
	public LinkedList<Row> getRows(String cat){
		return rows.get(cat);
	}
	
}
