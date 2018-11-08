package org.bibsonomy.webapp.controller;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.QueryScope;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ReportCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import java.util.LinkedList;
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

		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		int currentIndex = 0;
		boolean running= true;
		int limit = 300;
		List<Post<BibTex>> posts = new LinkedList<>();
		while (running) {
			int size = posts.size();
			posts.addAll(this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, null, null, QueryScope.LOCAL, null, null, null, null, currentIndex, currentIndex + 20));
			currentIndex = currentIndex + 20;
			running = (posts.size() - size) == 20 && currentIndex < limit;
		}
		command.setPosts(posts);

		return Views.REPORT;
	}

	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
