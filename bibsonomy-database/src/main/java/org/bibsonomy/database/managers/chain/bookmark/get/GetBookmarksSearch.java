package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
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

		if (this.db.isDoLuceneSearch()) {
			return this.db.getPostsSearchLucene(GroupID.PUBLIC.getId(), param.getRawSearch(), param.getRequestedUserName(), param.getUserName(), param.getGroupNames(), param.getLimit(), param.getOffset(), session);
		}
	
		return this.db.getPostsSearch(GroupID.PUBLIC.getId(), param.getRawSearch(), param.getRequestedUserName(), param.getLimit(), param.getOffset(), session);
	}
	
	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return ((param.getGrouping() == GroupingEntity.ALL || param.getGrouping() == GroupingEntity.USER) &&
				present(param.getSearch()));
	}
}