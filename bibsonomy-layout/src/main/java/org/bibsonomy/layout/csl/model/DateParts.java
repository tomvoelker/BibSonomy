package org.bibsonomy.layout.csl.model;

import java.util.ArrayList;

/**
 * DateParts in CSl are basically Lists of Strings.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class DateParts extends ArrayList<String> {

    /**
     * Constructor
     * 
     * @param year
     * @param month
     * @param day
     */
    public DateParts(String year, String month, String day) {
	super();
	this.add(year);
	this.add(month);
	this.add(day);
    }

    /**
     * Constructor 
     * 
     * @param year
     * @param month
     */
    public DateParts(String year, String month) {
	super();
	this.add(year);
	this.add(month);
    }

    
    /**
     * Constructor 
     * 
     * @param year
     */
    public DateParts(String year) {
	super();
	this.add(year);
    }
    
    
}
