package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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

	private static final Log LOGGER = LogFactory.getLog(GetBibTexByAuthor.class);

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		// uncomment following for a quick hack to access secondary datasource
		// session = this.dbSessionFactory.getDatabaseSession(DatabaseType.SLAVE);

		String searchMode = "";
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			searchMode = (String) envContext.lookup("searchMode");
		} catch (NamingException ex) {
			LOGGER.error("Error when trying to read environment variable 'searchmode' via JNDI.", ex);
		}
		
		if ("lucene".equals(searchMode)) {
			LOGGER.debug("Using Lucene in GetBibtexByAuthor");
			List<String> tagIndex = null;
			return this.db.getPostsByAuthorLucene(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getYear(), 
					param.getFirstYear(), param.getLastYear(), param.getLimit(), param.getOffset(), param.getSimHash(), tagIndex, session);
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