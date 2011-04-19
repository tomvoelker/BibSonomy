package org.bibsonomy.layout.csl.model;

import java.util.HashMap;

/**
 * A list of records according to CSL. Basically a HashMap with
 * the entry's ID as key.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class RecordList extends HashMap<String,Record> {

    public RecordList() {
	super();
    }
    
    public void add(Record rec) {
	this.put(rec.getId(), rec);
    }
    
}
