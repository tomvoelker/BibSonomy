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

package org.bibsonomy.model.logic.querybuilder;

import java.util.Date;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.ProjectSortKey;
import org.bibsonomy.model.enums.ProjectStatus;
import org.bibsonomy.model.logic.query.ProjectQuery;

/**
 * project query builder
 */
public class ProjectQueryBuilder extends BasicQueryBuilder<ProjectQueryBuilder> {
    /**
     * the order of the projects, default {@link ProjectSortKey#TITLE}
     */
    private ProjectSortKey sortKey = ProjectSortKey.TITLE;

    /**
     * the sort order of the order
     */
    private SortOrder sortOrder = SortOrder.ASC;

    private Prefix prefix;

    /**
     * the project status
     */
    private ProjectStatus projectStatus;

    private String type;
    private String sponsor;

    /**
     * the internalId
     */
    private String internalId;

    private Date startDate;

    private Date endDate;

    private Person person;
    private Group organization;

    /**
     *
     * @param person
     * @return
     */
    public ProjectQueryBuilder person(final Person person) {
        this.person = person;
        return this;
    }

    /**
     *
     * @param organization
     * @return
     */
    public ProjectQueryBuilder organization(final Group organization) {
        this.organization = organization;
        return this;
    }

    /**
     * @param prefix the prefix to query
     * @return the builder
     */
    public ProjectQueryBuilder prefix(final Prefix prefix) {
        this.prefix = prefix;
        return this;
    }

    /**
     * @param startDate
     * @return
     */
    public ProjectQueryBuilder startDate(final Date startDate) {
        this.startDate = startDate;
        return this;
    }

    /**
     * @param endDate
     * @return
     */
    public ProjectQueryBuilder endDate(final Date endDate) {
        this.endDate = endDate;
        return this;
    }

    /**
     * sets the internalId
     *
     * @param internalId
     * @return
     */
    public ProjectQueryBuilder internalId(final String internalId) {
        this.internalId = internalId;
        return this;
    }

    /**
     * sets the sort key
     *
     * @param sortKey
     * @return
     */
    public ProjectQueryBuilder sortKey(final ProjectSortKey sortKey) {
        this.sortKey = sortKey;
        return this;
    }

    /**
     * sets the sort order
     *
     * @param sortOrder
     * @return
     */
    public ProjectQueryBuilder sortOrder(final SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * sets the project status
     *
     * @param projectStatus
     * @return
     */
    public ProjectQueryBuilder projectStatus(final ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
        return this;
    }

    /**
     * sets the type
     *
     * @param type
     * @return
     */
    public ProjectQueryBuilder type(final String type) {
        this.type = type;
        return this;
    }

    /**
     * @param sponsor
     * @return
     */
    public ProjectQueryBuilder sponsor(String sponsor) {
        this.sponsor = sponsor;
        return this;
    }

    @Override
    protected ProjectQueryBuilder builder() {
        return this;
    }

    /**
     * @return the project query
     */
    public ProjectQuery build() {
        return new ProjectQuery(this.search, this.prefix, this.sortKey, this.sortOrder, this.projectStatus,
                this.type, this.sponsor, this.start, this.end, this.internalId, startDate, endDate, person, organization);
    }
}