/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.extra;

/**
 * Class to represent result of an elasticsearch aggregation count.
 *
 * @author kchoong
 */
public class SearchFilterElement implements Comparable<SearchFilterElement> {

    /**
     * unique name
     */
    private final String name;
    private final long count;

    /**
     * the elasticsearch index field
     */
    private String field;

    /**
     * fulltext search filter
     */
    private String filter;

    /**
     * message key to display label in webapp
     */
    private String messageKey;


    public SearchFilterElement(final String name, final long count) {
        this.name = name;
        this.count = count;
    }

    @Override
    public int compareTo(SearchFilterElement other) {
        int nameCompare = this.getName().compareTo(other.getName());
        if (nameCompare == 0) {
            return Long.compare(this.getCount(), other.getCount());
        }
        return nameCompare;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * @return the index field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the index field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the fulltext search filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @param filter the fulltext search filter to set
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * @return the message key
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * @param messageKey the message key to set
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
