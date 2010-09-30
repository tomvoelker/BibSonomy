package org.bibsonomy.database.managers.chain.goldstandard.publication.get;

import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.GoldStandardPublicationDatabaseManager;
import org.bibsonomy.database.managers.chain.ListChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;

/**
 * @author dzo
 * @version $Id$
 */
public class GoldStandardPublicationSearch extends ListChainElement<Post<GoldStandardPublication>, BibTexParam> {
    
    private final GoldStandardPublicationDatabaseManager manager = GoldStandardPublicationDatabaseManager.getInstance();
    
    @Override
    protected List<Post<GoldStandardPublication>> handle(BibTexParam param, DBSession session) {
	return manager.getSearcher().getPosts(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getGroupNames(), param.getSearch(), param.getTitle(), param.getAuthor(), null, null, null, null, param.getLimit(), param.getOffset());
    }

    @Override
    protected boolean canHandle(BibTexParam param) {
	return true; // currently there's only this chain element
    }

}
