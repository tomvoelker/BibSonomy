package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.RestProperties;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * shows all users bibsonomy has
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserListStrategy extends AbstractGetListStrategy<List<User>> {
	
	/**
	 * @param context
	 */
	public GetUserListStrategy(final Context context) {
		super(context);
	}
	
	@Override
	protected void appendLinkPostFix(StringBuilder sb) {
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder( RestProperties.getInstance().getApiUrl() + RestProperties.getInstance().getUsersUrl() );
	}

	@Override
	protected List<User> getList() {
		return this.getLogic().getUsers(null, GroupingEntity.ALL, null, null, null, null, null, null, this.getView().getStartValue(), this.getView().getEndValue());
	}

	@Override
	protected void render(Writer writer, List<User> resultList) {
		this.getRenderer().serializeUsers(writer, resultList, getView());
	}

	@Override
	public String getContentType() {
		return "users";
	}
}