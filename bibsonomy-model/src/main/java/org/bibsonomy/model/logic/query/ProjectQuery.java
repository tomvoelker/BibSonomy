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
			return new ProjectQuery(this.order, this.sortOrder, this.projectStatus, this.start, this.end);
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

	/**
	 * the constructor
	 * @param order
	 * @param sortOrder
	 * @param projectStatus
	 * @param start
	 * @param end
	 */
	protected ProjectQuery(ProjectOrder order, SortOrder sortOrder, ProjectStatus projectStatus, int start, int end) {
		this.order = order;
		this.sortOrder = sortOrder;
		this.projectStatus = projectStatus;
		this.start = start;
		this.end = end;
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
}
