package org.bibsonomy.database.managers.chain.goldstandard.get;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.goldstandard.GoldStandardChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author dzo
 * @version $Id$
 * @param <RR> 
 * @param <R> 
 * @param <P> 
 */
public class GetGoldStandardsByResourceSearch<RR extends Resource, R extends Resource & GoldStandard<RR>, P extends ResourceParam<RR>> extends GoldStandardChainElement<RR, R, P> {

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		return this.databaseManager.getSearch().getPosts(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getGroupNames(), param.getSearch(), param.getTitle(), param.getAuthor(), null, null, null, null, param.getLimit(), param.getOffset());
	}

	@Override
	protected boolean canHandle(final P param) {
		return true; // TODO: adapt
	}
}
