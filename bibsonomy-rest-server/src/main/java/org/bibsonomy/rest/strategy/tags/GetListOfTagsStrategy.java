package org.bibsonomy.rest.strategy.tags;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfTagsStrategy extends AbstractGetListStrategy<List<Tag>> {
	private final GroupingEntity grouping;
	private final String groupingValue;
	private final String regex;

	public GetListOfTagsStrategy(final Context context) {
		super(context);
		this.grouping = chooseGroupingEntity();
		
		if (this.grouping != GroupingEntity.ALL) {
			this.groupingValue = context.getStringAttribute(this.grouping.toString().toLowerCase(), null);
		} else {
			this.groupingValue = null;
		}

		this.regex = context.getStringAttribute("filter", null);
	}

	@Override
	protected void appendLinkPostFix(StringBuilder sb) {
		if (grouping != GroupingEntity.ALL && groupingValue != null) {
			sb.append("&").append(grouping.toString().toLowerCase()).append("=").append(groupingValue);
		}
		if (regex != null) {
			sb.append("&").append("filter=").append(regex);
		}
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder( RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getTagsUrl() );
	}

	@Override
	protected List<Tag> getList() {
		return this.getLogic().getTags(grouping, groupingValue, regex, this.getView().getStartValue(), this.getView().getEndValue());
	}

	@Override
	protected void render(Writer writer, List<Tag> resultList) {
		this.getRenderer().serializeTags(writer, resultList, this.getView());
	}

	@Override
	protected String getContentType() {
		return "tags";
	}

	
}