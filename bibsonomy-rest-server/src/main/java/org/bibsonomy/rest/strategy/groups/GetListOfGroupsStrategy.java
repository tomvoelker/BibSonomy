package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.RESTConfig;
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
	protected void appendLinkPostFix(final StringBuilder sb) {
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(this.getUrlRenderer().getApiUrl() + RESTConfig.GROUPS_URL);
	}

	@Override
	protected List<Group> getList() {
		return this.getLogic().getGroups(getView().getStartValue(), getView().getEndValue());
	}

	@Override
	protected void render(final Writer writer, final List<Group> resultList) {
		this.getRenderer().serializeGroups(writer, resultList, getView());
	}
}