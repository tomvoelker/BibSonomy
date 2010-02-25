package org.bibsonomy.database.managers.chain.bookmark.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.BookmarkChainElement;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;

/**
 * Returns all bookmarks whose title matches given title prefix
 * 
 * @author fei
 */
public class GetBookmarksByTitle extends BookmarkChainElement {

	@Override
	protected List<Post<Bookmark>> handle(final BookmarkParam param, final DBSession session) {
		// FIXME: we have to implement this method, when autocompletion for titles should work!
		//List<Post<BibTex>> posts = this.db.getPostsByTitleLucene(param.getTitle(), 0, null, param.getUserName(), param.getGroupNames(), param.getLimit(), param.getOffset(), session);
		List<Post<Bookmark>> posts = new LinkedList<Post<Bookmark>>();
		return posts;
	}

	@Override
	protected boolean canHandle(final BookmarkParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				present(param.getTitle())                 && 
				!present(param.getSearch())               &&
				!present(param.getHash())
				);
	}
}