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
package org.bibsonomy.model.logic.query;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.enums.ProjectOrder;
import org.bibsonomy.model.enums.ProjectStatus;

/**
 * the project query to retrieve projects from the logic
 * @author dzo
 */
public class ProjectQuery implements Query {

	public static class ProjectQueryBuilder {
		/** the order of the projects, default {@link ProjectOrder#TITLE} */
		private ProjectOrder order = ProjectOrder.TITLE;

		/** the sort order of the order */
		private SortOrder sortOrder = SortOrder.ASC;

		/** the project status */
		private ProjectStatus projectStatus;

		/** the start */
		private int start;

		/** the end */
		private int end;

		/** the externalId */
		private String externalId;

		/**
		 * sets the externalId
		 *
		 * @param externalId
		 * @return
		 */
		public ProjectQueryBuilder externalId(final String externalId) {
			this.externalId = externalId;
			return this;
		}

		/**
		 * sets the start
		 *
		 * @param start
		 * @return
		 */
		public ProjectQueryBuilder start(final int start) {
			this.start = start;
			return this;
		}

		/**
		 * sets the end
		 *
		 * @param end
		 * @return
		 */
		public ProjectQueryBuilder end(final int end) {
			this.end = end;
			return this;
		}

		/**
		 * sets the order
		 *
		 * @param order
		 * @return
		 */
		public ProjectQueryBuilder order(final ProjectOrder order) {
			this.order = order;
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
		 * @return the project query
		 */
		public ProjectQuery build() {
			return new ProjectQuery(this.order, this.sortOrder, this.projectStatus,
					this.start, this.end, this.externalId);
		}
	}

	/**
	 * @return creates a new builder
	 */
	public static ProjectQueryBuilder createBuilder() {
		return new ProjectQueryBuilder();
	}


	/** the order of the projects */
	private final ProjectOrder order;

	/** the sort order of the order */
	private final SortOrder sortOrder;

	/** the project status */
	private final ProjectStatus projectStatus;

	/** the start */
	private final int start;

	/** the end */
	private final int end;

	private final String externalId;

	/**
	 * the constructor
	 * @param order
	 * @param sortOrder
	 * @param projectStatus
	 * @param start
	 * @param end
	 * @param externalId
	 */
	protected ProjectQuery(ProjectOrder order, SortOrder sortOrder, ProjectStatus projectStatus, int start, int end, String externalId) {
		this.order = order;
		this.sortOrder = sortOrder;
		this.projectStatus = projectStatus;
		this.start = start;
		this.end = end;
		this.externalId = externalId;
	}

	/**
	 * @return the order
	 */
	public ProjectOrder getOrder() {
		return order;
	}

	/**
	 * @return the sortOrder
	 */
	public SortOrder getSortOrder() {
		return sortOrder;
	}

	/**
	 * @return the projectStatus
	 */
	public ProjectStatus getProjectStatus() {
		return projectStatus;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}
}
