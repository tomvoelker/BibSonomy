package org.bibsonomy.database.plugin.plugins;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.MetaDataPluginKey;
import org.bibsonomy.database.params.metadata.CopyPostParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * This plugin allows the saving of specific data.
 * 
 * @author clemensbaier
 * @version $Id$
 */
public class MetaDataPlugin extends AbstractDatabasePlugin {

	@Override
	public Runnable onPublicationInsert(final Post<? extends Resource> post, final DBSession session) {
		// check for copyFrom
		if (present(post) && present(post.getCopyFrom())) {
			return new Runnable() {
				@Override
				public void run() {
					final CopyPostParam param = new CopyPostParam();
					param.setInterHash(post.getResource().getInterHash());
					param.setIntraHash(post.getResource().getIntraHash());
					param.setValue(post.getCopyFrom());
					param.setUserName(post.getUser().getName());
					param.setKey(MetaDataPluginKey.COPY_PUBLICATION);
					insert("logPostCopy", param, session);
				}
			};
		}

		return null;
	}

	@Override
	public Runnable onBookmarkInsert(final Post<? extends Resource> post, final DBSession session) {
		// check for copyFrom
		if (present(post) && present(post.getCopyFrom())) {
			return new Runnable() {
				@Override
				public void run() {
					final CopyPostParam param = new CopyPostParam();
					param.setInterHash(post.getResource().getInterHash());
					param.setIntraHash(post.getResource().getIntraHash());
					param.setValue(post.getCopyFrom());
					param.setUserName(post.getUser().getName());
					param.setKey(MetaDataPluginKey.COPY_BOOKMARK);
					insert("logPostCopy", param, session);
				}
			};
		}

		return null;
	}
}
