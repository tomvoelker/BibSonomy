package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's for given author.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibTexByAuthor extends BibTexChainElement {

	private static final Log log = LogFactory.getLog(GetBibTexByAuthor.class);

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		// uncomment following for a quick hack to access secondary datasource
		// session = this.dbSessionFactory.getDatabaseSession(DatabaseType.SLAVE);

		if (this.db.isDoLuceneSearch()) {
			/*
			 * FIXME: why is the parameter "tagIndex" = null? 
			 */
			log.debug("Using Lucene in GetBibtexByAuthor");
			return this.db.getPostsByAuthorLucene(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getYear(), 
					param.getFirstYear(), param.getLastYear(), param.getLimit(), param.getOffset(), param.getSimHash(), null, session);
		}
		
		return this.db.getPostsByAuthor(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	
	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (present(param.getSearchEntity()) &&
				SearchEntity.AUTHOR.equals(param.getSearchEntity()) &&
				present(param.getSearch()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getTagIndex()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) );
	}	
}