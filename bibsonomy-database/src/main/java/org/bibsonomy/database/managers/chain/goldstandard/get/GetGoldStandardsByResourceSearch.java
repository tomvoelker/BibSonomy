package org.bibsonomy.database.managers.chain.goldstandard.get;

import java.util.List;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.goldstandard.GoldStandardChainElement;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.searcher.ResourceSearch;

/**
 * @author dzo
 * @param <RR> 
 * @param <R> 
 * @param <P> 
 */
public class GetGoldStandardsByResourceSearch<RR extends Resource, R extends Resource & GoldStandard<RR>, P extends ResourceParam<RR>> extends GoldStandardChainElement<RR, R, P> {

	@Override
	protected List<Post<R>> handle(final P param, final DBSession session) {
		ResourceSearch<R> search = this.databaseManager.getSearch();
		if (search == null) {
			// FIXME: should probably not be null, but was null for goldstandardbookmarks (test via http://localhost:8080/api/posts?resourcetype=goldstandardbookmark&sortPage=TITLE&sortOrder=ASC ) which might be at the wrong chain element here? 
			throw new UnsupportedResourceTypeException();
		}
		return search.getPosts(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), null, param.getGroupNames(), param.getSearch(), param.getTitle(), param.getAuthor(), null, null, null, null, null, param.getOrder(), param.getLimit(), param.getOffset());
	}

	@Override
	protected boolean canHandle(final P param) {
		return true; // TODO: adapt
	}
}
