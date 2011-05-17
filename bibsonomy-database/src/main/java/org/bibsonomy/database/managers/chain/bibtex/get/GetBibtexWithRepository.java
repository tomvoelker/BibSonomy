package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author philipp
 * @version $Id$
 */
public class GetBibtexWithRepository extends BibTexChainElement {

    @Override
    protected boolean canHandle(BibTexParam param) {
    	return (param.getFilter() == FilterEntity.POSTS_WITH_REPOSITORY);
    }

    @Override
    protected List<Post<BibTex>> handle(BibTexParam param, DBSession session) {
    	return db.getPostsWithRepository(param, session);
    }

}
