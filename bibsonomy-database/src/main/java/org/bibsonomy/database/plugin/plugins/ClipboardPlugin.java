/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.ClipboardDatabaseManager;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;

/**
 * Updates/Deletes items from the clipboard.
 * 
 * XXX: we can't have a static/singleton {@link ClipboardDatabaseManager} instance,
 * because we have a circular dependency (the manager contains the plugins ...)
 * could be fixed by config registry with Spring beans
 * 
 * @author daill
 */
public class ClipboardPlugin extends AbstractDatabasePlugin {
	
	private ClipboardDatabaseManager clipboardDatabaseManager;

	@Override
	public void onPublicationDelete(final int contentId, final DBSession session) {
		this.clipboardDatabaseManager.deleteItems(contentId, session);
	}
	
	@Override
	public void onBookmarkDelete(final int contentId, final DBSession session) {
		this.clipboardDatabaseManager.deleteItems(contentId, session);
	}
	
	@Override
	public void onPublicationUpdate(final int newContentId, final int contentId, final DBSession session) {
		this.clipboardDatabaseManager.updateItems(newContentId, contentId, session);
	}
	
	@Override
	public void onBookmarkUpdate(final int newContentId, final int contentId, final DBSession session){
		this.clipboardDatabaseManager.updateItems(newContentId, contentId, session);
	}

	/**
	 * @param clipboardDatabaseManager the clipboardDatabaseManager to set
	 */
	public void setClipboardDatabaseManager(ClipboardDatabaseManager clipboardDatabaseManager) {
		this.clipboardDatabaseManager = clipboardDatabaseManager;
	}
}
