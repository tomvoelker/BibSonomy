package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given author.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByAuthor extends TagChainElement {
	private static final Log log = LogFactory.getLog(GetTagsByAuthor.class);
	
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		String searchMode = "";
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			searchMode = (String) envContext.lookup("searchMode");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variable 'searchmode' via JNDI.", ex);
		}
		
		if ("lucene".equals(searchMode)) {
			// FIXME: which parameters do we actually need?
			return this.db.getTagsByAuthorLucene(param.getRawSearch(), param.getGroupType(), param.getRequestedUserName(), param.getRequestedGroupName(), null, null, null, param.getSimHash(), null, session);
		}
		
		return this.db.getTagsByAuthor(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (SearchEntity.AUTHOR.equals(param.getSearchEntity()) &&
				!present(param.getTagIndex()) &&
				!present(param.getBibtexKey()) &&
				present(param.getSearch()));
	}
}