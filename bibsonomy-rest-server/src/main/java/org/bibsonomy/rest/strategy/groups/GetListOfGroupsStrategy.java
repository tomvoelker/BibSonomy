package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetListOfGroupsStrategy extends AbstractGetListStrategy<List<Group>> {
	
	/**
	 * @param context
	 */
	public GetListOfGroupsStrategy(final Context context) {
		super(context);
	}

	@Override
	public String getContentType() {
		return "groups";
	}

	@Override
	protected void appendLinkPostFix(StringBuilder sb) {
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getGroupsUrl());
	}

	@Override
	protected List<Group> getList() {
		return this.getLogic().getGroups(getView().getStartValue(), getView().getEndValue());
	}

	@Override
	protected void render(Writer writer, List<Group> resultList) {
		this.getRenderer().serializeGroups(writer, resultList, getView());
	}
}