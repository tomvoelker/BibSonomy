package org.bibsonomy.rest.strategy.tags;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Christian Kramer
 * @version $Id$
 */
public class GetListOfTagsStrategy extends AbstractGetListStrategy<List<Tag>> {
	protected final Class<? extends Resource> resourceType;
	private final GroupingEntity grouping;
	private final String groupingValue;
	private final String regex;
	private final String hash;
	
	/**
	 * @param context
	 */
	public GetListOfTagsStrategy(final Context context) {
		super(context);
		this.grouping = chooseGroupingEntity();
		this.resourceType = Resource.getResource(context.getStringAttribute("resourcetype", "all"));
		this.hash = context.getStringAttribute("resource", null);
		
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
		if (this.getView().getOrder() == Order.FREQUENCY) {
			sb.append("&").append("order=").append(this.getView().getOrder().toString().toLowerCase());
		}
		if (resourceType != Resource.class) {
			sb.append("&resourcetype=").append(Resource.toString(resourceType).toLowerCase());
		}
		if (hash != null) {
			sb.append("&resource=").append(hash);
		}		
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder( RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getTagsUrl() );
	}

	@Override
	protected List<Tag> getList() {
		return this.getLogic().getTags(resourceType, grouping, groupingValue, regex, null, hash, this.getView().getOrder(), this.getView().getStartValue(), this.getView().getEndValue(), null, null);
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