package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author philipp
 * @version $Id$
 */
public class GetBibtexWithRepository extends ResourceChainElement<BibTex, BibTexParam> {

    @Override
    protected boolean canHandle(final BibTexParam param) {
    	return (param.getFilter() == FilterEntity.POSTS_WITH_REPOSITORY);
    }

    @Override
    protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
    	return ((BibTexDatabaseManager) this.databaseManager).getPostsWithRepository(param, session);
    }

}
