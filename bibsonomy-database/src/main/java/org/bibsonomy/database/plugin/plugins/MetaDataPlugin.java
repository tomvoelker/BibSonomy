/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
