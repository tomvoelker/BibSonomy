/**
 * BibSonomy-Database - Database for BibSonomy.
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
package org.bibsonomy.database.managers.fixtures;

import org.bibsonomy.common.enums.Privlevel;

/**
 * A fixture providing the expected values for properties of the "group_basic" type. See GroupCommon.xml for details.
 */
public class BasicGroupFixture {

    private int groupId;
    private String name;
    private Privlevel privlevel;
    private boolean sharedDocuments;
    private boolean allowjoin;
    private String description;
    private boolean organization;

    public BasicGroupFixture(int groupId,
                             String name,
                             Privlevel privlevel,
                             boolean sharedDocuments,
                             boolean allowjoin,
                             String description,
                             boolean organization) {
        this.groupId = groupId;
        this.name = name;
        this.privlevel = privlevel;
        this.sharedDocuments = sharedDocuments;
        this.allowjoin = allowjoin;
        this.description = description;
        this.organization = organization;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public Privlevel getPrivlevel() {
        return privlevel;
    }

    public boolean isSharedDocuments() {
        return sharedDocuments;
    }

    public boolean isAllowjoin() {
        return allowjoin;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOrganization() {
        return organization;
    }
}
