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
package org.bibsonomy.search.index.database.project;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Date;

import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.database.project.mybatis.ProjectIndexInformationMapper;
import org.bibsonomy.search.model.SearchIndexState;

/**
 * the database information logic for {@link org.bibsonomy.model.cris.Project}s
 *
 * @author dzo
 */
public class ProjectDatabaseInformationLogic implements DatabaseInformationLogic<SearchIndexState> {
	private ProjectIndexInformationMapper projectIndexInformationMapper;

	@Override
	public SearchIndexState getDbState() {
		if (this.projectIndexInformationMapper == null) {
			throw new IllegalStateException("No project information mapper configured");
		}
		final SearchIndexState searchIndexState = new SearchIndexState();
		final Integer lastId = this.projectIndexInformationMapper.getLastProjectChangeId(ConstantID.PROJECT_ID.getId());
		searchIndexState.setEntityId(lastId);
		Date logDate = this.projectIndexInformationMapper.getLastProjectChangeLogDate();
		// if there is no log entry return the current date time as last log date
		if (!present(logDate)) {
			logDate = new Date();
		}
		searchIndexState.setEntityLogDate(logDate);
		return searchIndexState;
	}

	/**
	 * @param projectIndexInformationMapper
	 *            mapper used for project index state queries
	 */
	public void setProjectIndexInformationMapper(final ProjectIndexInformationMapper projectIndexInformationMapper) {
		this.projectIndexInformationMapper = projectIndexInformationMapper;
	}
}
