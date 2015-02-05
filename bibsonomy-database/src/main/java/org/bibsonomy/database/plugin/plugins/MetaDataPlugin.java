package org.bibsonomy.database.plugin.plugins;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.MetaDataPluginKey;
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
	public void onPublicationInsert(final Post<? extends Resource> post, final DBSession session) {
		// check for copyFrom
		if (present(post) && present(post.getCopyFrom())) {
			final PostParam param = createParam(post, MetaDataPluginKey.COPY_PUBLICATION);
			this.insert("logPostCopy", param, session);
		}
	}

	@Override
	public void onBookmarkInsert(final Post<? extends Resource> post, final DBSession session) {
		// check for copyFrom
		if (present(post) && present(post.getCopyFrom())) {
			final PostParam param = createParam(post, MetaDataPluginKey.COPY_BOOKMARK);
			this.insert("logPostCopy", param, session);
		}
	}
	
	private static PostParam createParam(final Post<? extends Resource> post, final MetaDataPluginKey key) {
		final PostParam param = new PostParam();
		param.setInterHash(post.getResource().getInterHash());
		param.setIntraHash(post.getResource().getIntraHash());
		param.setValue(post.getCopyFrom());
		param.setUserName(post.getUser().getName());
		param.setKey(key);
		return param;
	}
}
