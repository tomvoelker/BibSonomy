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
 * A fixture providing the expected values for properties of the "group_extended" type. See GroupCommon.xml for details.
 */
public class ExtendedGroupFixture extends BasicGroupFixture {

    private final String realName;
    private final String homepage;

    public ExtendedGroupFixture(int id,
                                String name,
                                Privlevel privlevel,
                                boolean sharedDocuments,
                                boolean allowjoin,
                                String description,
                                boolean organization,
                                String realName,
                                String homepage) {

        super(id, name, privlevel, sharedDocuments, allowjoin, description, organization);

        this.realName = realName;
        this.homepage = homepage;
    }

    public String getRealName() {
        return realName;
    }

    public String getHomepage() {
        return homepage;
    }
}
