package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.ValidationException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfGroupsStrategy extends Strategy {

	public GetListOfGroupsStrategy(final Context context) {
		super(context);
	}

	@Override
	public void validate() throws ValidationException {
		// should be ok for everybody
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		// setup viewModel
		final int start = this.context.getIntAttribute("start", 0);
		int end = this.context.getIntAttribute("end", 20);

		final List<Group> groups = this.context.getLogic().getGroups(this.context.getAuthUserName(), start, end);

		final ViewModel viewModel = new ViewModel();
		if (groups.size() < end || groups.size() > end) {
			end = groups.size();
		} else {
			final String next = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getGroupsUrl() + "?start=" + String.valueOf(end) + "&end=" + String.valueOf(end + 20);
			viewModel.setUrlToNextResources(next);
		}
		viewModel.setStartValue(start);
		viewModel.setEndValue(end);

		// delegate to the renderer
		this.context.getRenderer().serializeGroups(writer, groups, viewModel);
	}

	@Override
	public String getContentType(final String userAgent) {
		if (this.context.apiIsUserAgent(userAgent)) return "bibsonomy/groups+" + this.context.getRenderingFormat().toString();
		return RestProperties.getInstance().getContentType();
	}
}