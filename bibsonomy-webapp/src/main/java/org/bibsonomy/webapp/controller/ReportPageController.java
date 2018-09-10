package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.logic.LogicInterface;Ø
import org.bibsonomy.webapp.command.ReportCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

public class ReportPageController implements MinimalisticController<ReportCommand> {
	private LogicInterface logic;

	@Override
	public ReportCommand instantiateCommand() {
		return new ReportCommand();
	}

	@Override
	public View workOn(ReportCommand command) {
		final RequestWrapperContext context = command.getContext();

		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		final List<Post<T>> posts = logic.getPosts();
		command.setPosts(posts);

		return Views.REPORT;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
