/**
 * BibSonomy-Layout - Layout engine for the webapp.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.layout.csl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a date representation for entries according to CSL.
 * See http://gsl-nagoya-u.net/http/pub/citeproc-doc.html#dates
 *
 * @author Dominik Benz, benz@cs.uni-kassel.de
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
