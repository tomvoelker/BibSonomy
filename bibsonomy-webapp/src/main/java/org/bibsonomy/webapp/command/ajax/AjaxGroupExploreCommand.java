/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.webapp.command.GroupExploreViewCommand;

/**
 * Command for group explore AJAX calls
 *
 * @author kchoong
 */
public class AjaxGroupExploreCommand extends GroupExploreViewCommand {

    /**
     * page of pagination
     */
    private int page;
    /**
     * entries per page
     */
    private int pageSize;

    /**
     * flag to set, if call should retrieve just the distinct counts
     */
    private boolean distinctCount;

    /**
     * JSON response string
     */
    private String responseString;

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the page size to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the distinct count flag
     */
    public boolean isDistinctCount() {
        return distinctCount;
    }

    /**
     * @param distinctCount set the distinct count flag
     */
    public void setDistinctCount(boolean distinctCount) {
        this.distinctCount = distinctCount;
    }

    /**
     * @return the response string
     */
    public String getResponseString() {
        return responseString;
    }

    /**
     * @param responseString the response string to set
     */
    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}
