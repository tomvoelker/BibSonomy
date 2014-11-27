/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
