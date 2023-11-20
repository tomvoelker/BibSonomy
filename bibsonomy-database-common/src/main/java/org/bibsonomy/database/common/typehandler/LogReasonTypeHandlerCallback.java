/**
 * BibSonomy-Database-Common - Helper classes for database interaction
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
package org.bibsonomy.database.common.typehandler;

import org.bibsonomy.database.common.enums.LogReason;
import org.bibsonomy.util.collection.BiHashMap;
import org.bibsonomy.util.collection.BiMap;

/**
 * type handler for {@link LogReason}
 *
 * @author dzo
 */
public class LogReasonTypeHandlerCallback extends AbstractEnumTypeHandlerCallback<LogReason> {

	private static final BiMap<LogReason, Integer> LOG_REASON_MAPPING = new BiHashMap<>();

	static{
		LOG_REASON_MAPPING.put(LogReason.UPDATED, 0);
		LOG_REASON_MAPPING.put(LogReason.DELETED, 1);
		LOG_REASON_MAPPING.put(LogReason.LINKED_ENTITY_UPDATE, 2);
	}

	@Override
	protected LogReason getDefaultValue() {
		return LogReason.DELETED;
	}

	@Override
	protected BiMap<LogReason, Integer> getMapping() {
		return LOG_REASON_MAPPING;
	}
}
