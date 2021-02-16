package org.bibsonomy.database.managers.metadata;

import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.database.services.ProjectSearch;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.util.object.FieldDescriptor;

/**
 * @author dzo
 */
public class ProjectMetaDataAdapter<E> implements Function<FieldDescriptor<Project, E>, Set<E>> {

	private ProjectSearch projectSearch;

	/**
	 * @param projectSearch
	 */
	public ProjectMetaDataAdapter(ProjectSearch projectSearch) {
		this.projectSearch = projectSearch;
	}

	@Override
	public Set<E> apply(FieldDescriptor<Project, E> fieldDescriptor) {
		return this.projectSearch.getDistinctFieldValues(fieldDescriptor);
	}
}
