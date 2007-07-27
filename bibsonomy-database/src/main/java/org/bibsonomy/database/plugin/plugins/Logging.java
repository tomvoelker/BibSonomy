package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.GroupParam;
import org.bibsonomy.database.params.beans.TagRelationParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.util.DBSession;

/**
 * This plugin implements logging: on several occasions it'll save the old state
 * of objects (bookmarks, publications, etc.) into special tables in the
 * database. This way it is possible to track the changes made by users.
 * 
 * @author Jens Illig
 * @author Christian Schenk
 * @author Anton Wilhelm
 * @version $Id$
 */
public class Logging extends AbstractDatabasePlugin {

	@Override
	public Runnable onBibTexDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				final BibTexParam param = new BibTexParam();
				param.setRequestedContentId(contentId);
				insert("logBibTex", param, session);
			}
		};
	}

	@Override
	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				final BibTexParam param = new BibTexParam();
				param.setRequestedContentId(contentId);
				insert("logBibTex", param, session);
				param.setNewContentId(newContentId);
				insert("logBibTexUpdate", param, session);
			}
		};
	}


	@Override
	public Runnable onBookmarkDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				final BookmarkParam param = new BookmarkParam();
				param.setRequestedContentId(contentId);
				insert("logBookmark", param, session);
			}
		};
	}


	@Override
	public Runnable onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				final BookmarkParam param = new BookmarkParam();
				param.setRequestedContentId(contentId);
				insert("logBookmark", param, session);
				param.setNewContentId(newContentId);
				insert("logBookmarkUpdate", param, session);
			}
		};
	}

	@Override
	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final DBSession session) {
		return new Runnable() {
			public void run() {
				final TagRelationParam trp = new TagRelationParam();
				trp.setOwnerUserName(userName);
				trp.setLowerTagName(lowerTagName);
				trp.setUpperTagName(upperTagName);
				insert("logTagRelation", trp, session);
			}
		};
	}

	@Override
	public Runnable onTagDelete(final int contentId, final DBSession session) {
		return new Runnable() {
			public void run() {
				final TagParam param = new TagParam();
				param.setRequestedContentId(contentId);
				insert("logTasDelete", param, session);
			}
		};
	}

	@Override
	public Runnable onDeleteUserfromGroup(final String userName, final int groupId, final DBSession session) {
		return new Runnable() {
			public void run() {
				final GroupParam groupParam = new GroupParam();
				final BibTexParam bibParam = new BibTexParam();
				final BookmarkParam bookParam = new BookmarkParam();
				groupParam.setGroupId(groupId);
				groupParam.setUserName(userName);
				bibParam.setGroupId(groupId);
				bibParam.setUserName(userName);
				bookParam.setGroupId(groupId);
				bookParam.setUserName(userName);
				insert("logGroupDeleteUser", groupParam, session);
				insert("logBibtexDeleteUserfromGroup", bibParam, session);
				insert("logBookmarkDeleteUserfromGroup", bookParam, session);
			}
		};
	}
}