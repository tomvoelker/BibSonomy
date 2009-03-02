package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.common.enums.DatabaseType;

/**
 * Returns a list of BibTex's for given tag/tags and author.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibTexByAuthorAndTag extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, DBSession session) {
		// uncomment following for a quick hack to access secondary datasource
		// session = this.dbSessionFactory.getDatabaseSession(DatabaseType.SLAVE);		
		return this.db.getBibTexByAuthorAndTag(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (SearchEntity.AUTHOR.equals(param.getSearchEntity()) &&
				present(param.getSearch()) && 
				!present(param.getBibtexKey()) &&
				present(param.getTagIndex()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED));
	}
}