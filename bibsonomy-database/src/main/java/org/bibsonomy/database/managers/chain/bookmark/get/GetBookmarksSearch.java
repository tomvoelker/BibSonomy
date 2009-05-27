package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Returns a list of bookmarks for a given fulltext search string.
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetBookmarksSearch extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, DBSession session) {
		// uncomment following for a quick hack to access secondary datasource
		// session = this.dbSessionFactory.getDatabaseSession(DatabaseType.SLAVE);

		Context initContext = null;
		Context envContext = null;
		String searchMode = "";
		try {
			initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env");
			searchMode = (String) envContext.lookup("searchMode");
		} catch (NamingException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		if ("lucene".equals(searchMode)) {
			return this.db.getBookmarkSearchLucene(param.getGroupId(), param.getSearch(), param.getRequestedUserName(), param.getUserName(), param.getGroupNames(), param.getLimit(), param.getOffset(), session);
		}
	
		return this.db.getBookmarkSearch(param.getGroupType(), param.getSearch(), param.getRequestedUserName(), param.getLimit(), param.getOffset(), session);
		
		
/*		
		if (SearchEntity.LUCENE.equals(param.getSearchEntity())) {
			//param.getGroups();   // gruppen, die der eingeloggte user sehen darf
			//param.getUserName(); // eingeloggter user
			//param.getGroupNames(); // gruppennamen der gruppen, die der eingeloggte user sehen darf
			return this.db.getBookmarkSearchLucene(param.getGroupId(), param.getSearch(), param.getRequestedUserName(), param.getUserName(), param.getGroupNames(), param.getLimit(), param.getOffset(), session);
		}
		return this.db.getBookmarkSearch(param.getGroupType(), param.getSearch(), param.getRequestedUserName(), param.getLimit(), param.getOffset(), session);
*/
	}
	
	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return ((param.getGrouping() == GroupingEntity.ALL || param.getGrouping() == GroupingEntity.USER) &&
				present(param.getSearch()));
	}
}