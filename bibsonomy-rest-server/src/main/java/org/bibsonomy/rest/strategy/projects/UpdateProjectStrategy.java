package org.bibsonomy.rest.strategy.projects;

import java.io.Writer;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.ValidationUtils;

/**
 * strategy to update a project
 *
 * @author pda
 */
public class UpdateProjectStrategy extends AbstractUpdateStrategy {
	private final String externalId;

	public UpdateProjectStrategy(final Context context, final String externalId) {
		super(context);
		ValidationUtils.requirePresent(externalId, "No externalId given.");
		this.externalId = externalId;
	}

	@Override
	protected void render(final Writer writer, final String resourceID) {
		this.getRenderer().serializeProjectId(writer,resourceID);
	}

	@Override
	protected String update() {
		final Project project = this.getRenderer().parseProject(this.doc);
		this.getLogic().updateProject(this.externalId, project);
		return project.getExternalId();
	}
}
