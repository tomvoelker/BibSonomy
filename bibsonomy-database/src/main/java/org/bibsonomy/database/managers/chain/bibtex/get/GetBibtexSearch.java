package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetBibtexSearch extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, DBSession session) {
		// uncomment following for a quick hack to access secondary datasource
		// session = this.dbSessionFactory.getDatabaseSession(DatabaseType.SLAVE);

		if (this.db.isDoLuceneSearch()) {
			return this.db.getPostsSearchLucene(GroupID.INVALID.getId(), param.getRawSearch(), param.getRequestedUserName(), param.getUserName(), param.getGroupNames(), param.getLimit(), param.getOffset(), session);
		}

		// default = database
		return this.db.getPostsSearch(GroupID.PUBLIC.getId(), param.getRawSearch(), param.getRequestedUserName(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return ((param.getGrouping() == GroupingEntity.ALL || param.getGrouping() == GroupingEntity.USER) &&
				(SearchEntity.ALL.equals(param.getSearchEntity()) || SearchEntity.LUCENE.equals(param.getSearchEntity()) ) &&				
				!present(param.getBibtexKey()) &&
				present(param.getSearch()));
	}
}