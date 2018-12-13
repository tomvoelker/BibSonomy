package org.bibsonomy.rest.strategy.projects;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

import java.io.Writer;
import java.util.List;

public class GetProjectsStrategy extends AbstractGetListStrategy<List<Project>> {
	private final String internalId;

	public GetProjectsStrategy(Context context) {
		super(context);
		internalId = context.getStringAttribute("internalId", null);
	}

	@Override
	protected String getContentType() {
		return "projects";
	}

	@Override
	protected void render(Writer writer, List<Project> resultList) {
		this.getRenderer().serializeProjects(writer, resultList, this.getView());
	}

	@Override
	protected List<Project> getList() {
		final ViewModel viewModel = this.getView();
		final ProjectQuery projectQuery = new ProjectQuery.ProjectQueryBuilder().internalId(this.internalId).
						start(viewModel.getStartValue()).end(viewModel.getEndValue()).build();
		return this.getLogic().getProjects(projectQuery);
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForProjects();
	}
}
