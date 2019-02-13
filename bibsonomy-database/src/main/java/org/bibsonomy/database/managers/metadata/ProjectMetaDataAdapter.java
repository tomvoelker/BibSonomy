package org.bibsonomy.database.managers.metadata;

import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.services.searcher.ProjectSearch;

/**
 * @author dzo
 */
public class ProjectMetaDataAdapter<E> implements Function<Function<Project, E>, Set<E>> {

	private ProjectSearch projectSearch;

	/**
	 * @param projectSearch
	 */
	public ProjectMetaDataAdapter(ProjectSearch projectSearch) {
		this.projectSearch = projectSearch;
	}

	@Override
	public Set<E> apply(final Function<Project, E> getter) {
		return this.projectSearch.getDistinctFieldValues(getter);
	}
}
