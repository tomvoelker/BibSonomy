package org.bibsonomy.rest.strategy.tags;

import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfTagsStrategy extends Strategy {

	public GetListOfTagsStrategy(final Context context) {
		super(context);
	}

	@Override
	public void perform(final HttpServletRequest request, final Writer writer) throws InternServerException {
		// setup viewModel
		final int start = this.context.getIntAttribute("start", 0);
		int end = this.context.getIntAttribute("end", 20);

		final GroupingEntity grouping = chooseGroupingEntity();
		final String groupingValue;
		if (grouping != GroupingEntity.ALL) {
			groupingValue = this.context.getStringAttribute(grouping.toString().toLowerCase(), null);
		} else {
			groupingValue = null;
		}

		final String regex = this.context.getStringAttribute("filter", null);

		final List<Tag> tags = this.context.getLogic().getTags(grouping, groupingValue, regex, start, end);

		final ViewModel viewModel = new ViewModel();
		if (tags.size() < end || tags.size() > end) {
			end = tags.size();
		} else {
			String next = RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getTagsUrl() + "?start=" + String.valueOf(end) + "&end=" + String.valueOf(end + 20);
			if (grouping != GroupingEntity.ALL && groupingValue != null) {
				next += "&" + grouping.toString().toLowerCase() + "=" + groupingValue;
			}
			if (regex != null) {
				next += "&" + "filter=" + regex;
			}
			viewModel.setUrlToNextResources(next);
		}
		viewModel.setStartValue(start);
		viewModel.setEndValue(end);

		// delegate to the renderer
		this.context.getRenderer().serializeTags(writer, tags, viewModel);
	}

	@Override
	public String getContentType(final String userAgent) {
		if (this.context.apiIsUserAgent(userAgent)) return "bibsonomy/tags+" + this.context.getRenderingFormat().toString();
		return RestProperties.getInstance().getContentType();
	}
}