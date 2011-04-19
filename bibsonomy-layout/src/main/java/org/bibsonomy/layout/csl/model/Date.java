package org.bibsonomy.layout.csl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a date representation for entries according to CSL.
 * See http://gsl-nagoya-u.net/http/pub/citeproc-doc.html#dates
 *
 * @author Dominik Benz, benz@cs.uni-kassel.de
 * @version $Id$
 */
public class Date {
    
    /**
     * Constructor
     */
    public Date() {
	this.date_parts = new ArrayList<DateParts>();
    }
    
    // a list of date parts
    private List<DateParts> date_parts;
    
    // literal date
    private String literal;
    // ca argument
    private String circa;
    // season
    private String season;
    
    //*************************************************
    // getter / setter
    //*************************************************
    
    public List<DateParts> getDate_parts() {
        return date_parts;
    }

    public void setDate_parts(List<DateParts> date_parts) {
        this.date_parts = date_parts;
    }    
    
    public String getCirca() {
        return circa;
    }

    public void setCirca(String circa) {
        this.circa = circa;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }    
}
