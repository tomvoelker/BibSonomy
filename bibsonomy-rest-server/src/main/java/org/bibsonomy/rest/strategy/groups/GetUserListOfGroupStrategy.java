package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserListOfGroupStrategy extends Strategy {

	private final String groupName;

	public GetUserListOfGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
	}

	@Override
	public void validate() throws ValidationException {
		// TODO check if the authuser is allowed to the the groupmembers
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		// setup viewModel
		final int start = this.context.getIntAttribute("start", 0);
		int end = this.context.getIntAttribute("end", 20);

		final List<User> users = this.context.getLogic().getUsers(this.context.getAuthUserName(), this.groupName, start, end);

		final ViewModel viewModel = new ViewModel();
		if (users.size() < end || users.size() > end) {
			end = users.size();
		} else {
			final String next = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getGroupsUrl() + "/" + groupName + "/" + RestProperties.getInstance().getUsersUrl() + "?start=" + String.valueOf(end) + "&end=" + String.valueOf(end + 20);
			viewModel.setUrlToNextResources(next);
		}
		viewModel.setStartValue(start);
		viewModel.setEndValue(end);

		// delegate to the renderer
		this.context.getRenderer().serializeUsers(writer, users, viewModel);
	}

	@Override
	public String getContentType(final String userAgent) {
		if (this.context.apiIsUserAgent(userAgent)) return "bibsonomy/users+" + this.context.getRenderingFormat().toString();
		return RestProperties.getInstance().getContentType();
	}
}