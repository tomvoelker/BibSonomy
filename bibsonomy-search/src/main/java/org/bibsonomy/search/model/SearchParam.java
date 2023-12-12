/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.model;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.database.common.enums.CRISEntityType;

import java.util.Date;

/**
 * Class for search queries
 *
 * @author Jens Illig
 */
@Getter
@Setter
public class SearchParam {

    private String userName;

    /** The SQL-Limit */
    private int limit;

    /** The SQL-Offset */
    private int offset;

    /** newest tas_id during last index update */
    private Integer lastTasId;

    private long lastContentId;

    private int lastOffset; // TODO or just use offset?

    /** newest change_date during last index update */
    private Date lastLogDate;

    private Date lastDate;

    private Date lastDocumentDate;

    private String userRelation;

    private boolean includeRelatedEntityUpdates;

    private CRISEntityType sourceType;

    private CRISEntityType targetType;

}