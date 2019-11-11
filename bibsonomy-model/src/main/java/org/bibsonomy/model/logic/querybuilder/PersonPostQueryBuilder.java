/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.logic.querybuilder;

/**
 * @author kchoong
 */
public class PersonPostQueryBuilder {

    private String personId;
    private boolean paginated;
    private int start;
    private int end;

    /**
     * Retrieve only resources from [<code>start</code>; <code>end</code>).
     *
     * @param start index of the first item.
     * @param end index of the last item.
     *
     * @return the builder.
     */
    public PersonPostQueryBuilder fromTo(int start, int end) {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException(String.format("Indices must be >= 0. start=%d, end=%d", start, end));
        }

        if (start > end) {
            throw new IllegalArgumentException(String.format("start must be <= end: %d > %d", start, end));
        }

        this.paginated = true;
        this.start = start;
        this.end = end;

        return this;
    }


    public String getPersonId() {
        return personId;
    }

    public PersonPostQueryBuilder setPersonId(String personId) {
        this.personId = personId;
        return this;
    }

    public boolean isPaginated() {
        return paginated;
    }

    public PersonPostQueryBuilder setPaginated(boolean paginated) {
        this.paginated = paginated;
        return this;
    }

    public int getStart() {
        return start;
    }

    public PersonPostQueryBuilder setStart(int start) {
        this.start = start;
        return this;
    }

    public int getEnd() {
        return end;
    }

    public PersonPostQueryBuilder setEnd(int end) {
        this.end = end;
        return this;
    }
}
