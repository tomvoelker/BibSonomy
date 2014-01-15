package org.bibsonomy.database.plugin.plugins;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.MetaDataPluginKey;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.database.params.metadata.PostParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * This plugin allows the saving of specific data.
 * 
 * @author clemensbaier
 */
public class MetaDataPlugin extends AbstractDatabasePlugin {
	@Override
	public void onPublicationDelete(final int contentId, final DBSession session) {
		// TODO: what to do ?
	}

	@Override
	public void onPublicationUpdate(final int newContentId, final int contentId, final DBSession session) {
		PostParam param = new PostParam();
		param.setNewContentId(newContentId);
		param.setContentId(contentId);
		this.update("updateMetaDataPostContentId", param, session);
		this.update("updateMetaDataPostRefContentId", param, session);
	}

	@Override
	public void onPublicationInsert(final Post<? extends Resource> post, final DBSession session) {
		// check for copyFrom
		if (present(post)) {
			if (present(post.getCopyFrom()) & present(post.getCopyIntraHash())) {
				int refContentId = BibTexDatabaseManager.getInstance().getContentIdForPost(post.getCopyIntraHash(), post.getCopyFrom(), session);
				final PostParam param = createParam(post, MetaDataPluginKey.COPY_PUBLICATION, refContentId);
				this.insert("logPostCopy", param, session);
			}
		}
	}

	@Override
	public void onBookmarkInsert(final Post<? extends Resource> post, final DBSession session) {
		// check for copyFrom
		if (present(post)) {
			if (present(post.getCopyFrom()) & present(post.getCopyIntraHash())) {
				int refContentId = BookmarkDatabaseManager.getInstance().getContentIdForPost(post.getCopyIntraHash(), post.getCopyFrom(), session);
				final PostParam param = createParam(post, MetaDataPluginKey.COPY_BOOKMARK, refContentId);
				this.insert("logPostCopy", param, session);
			}
		}
	}

	private static PostParam createParam(final Post<? extends Resource> post, final MetaDataPluginKey key, final int refContentId) {
		final PostParam param = new PostParam();
		param.setInterHash(post.getResource().getInterHash());
		param.setIntraHash(post.getResource().getIntraHash());
		param.setValue(post.getCopyFrom());
		param.setUserName(post.getUser().getName());
		param.setKey(key);
		param.setContentId(post.getContentId());
		param.setRefContentId(refContentId);
		return param;
	}
}
