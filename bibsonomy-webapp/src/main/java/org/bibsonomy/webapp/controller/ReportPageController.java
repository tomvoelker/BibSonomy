package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ReportCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.List;

public class ReportPageController implements MinimalisticController<ReportCommand> {
	private LogicInterface logic;

	@Override
	public ReportCommand instantiateCommand() {
		return new ReportCommand();
	}

	@Override
	public View workOn(ReportCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		// todo loop
		final List<Post<BibTex>> posts = this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, null, null, null, null, null, null, 0, 20);
		command.setPosts(posts);

		return Views.REPORT;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
