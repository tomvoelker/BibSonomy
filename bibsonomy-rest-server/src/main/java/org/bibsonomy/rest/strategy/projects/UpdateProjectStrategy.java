package org.bibsonomy.rest.strategy.projects;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.ValidationUtils;

import java.io.Writer;

public class UpdateProjectStrategy extends AbstractUpdateStrategy {
	private final String externalId;

	public UpdateProjectStrategy(Context context, String externalId) {
		super(context);
		ValidationUtils.requirePresent(externalId, "No externalId given.");
		this.externalId = externalId;
	}

	@Override
	protected void render(Writer writer, String resourceID) {
		this.getRenderer().serializeProjectId(writer,resourceID);
	}

	@Override
	protected String update() {
		final Project project = this.getRenderer().parseProject(this.doc);
		this.getLogic().updateProject(this.externalId, project);
		return project.getExternalId();
	}
}
