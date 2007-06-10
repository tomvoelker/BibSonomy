package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.beans.TagRelationParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.database.util.Transaction;

/**
 * This plugin implements logging: on several occasions it'll save the old state
 * of objects (bookmarks, publications, etc.) into special tables in the
 * database. This way it is possible to track the changes made by users.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class Logging extends AbstractDatabasePlugin {

	@Override
	public Runnable onBibTexInsert(final int contentId, final Transaction session) {
		return new Runnable() {
			public void run() {
				final BibTexParam param = new BibTexParam();
				param.setRequestedContentId(contentId);
				insert("logBibTex", param, session);
			}
		};
	}

	@Override
	public Runnable onBibTexUpdate(final int newContentId, final int contentId, final Transaction session) {
		return new Runnable() {
			public void run() {
				final BibTexParam param = new BibTexParam();
				param.setRequestedContentId(contentId);
				param.setNewContentId(newContentId);
				insert("logBibTexUpdate", param, session);
			}
		};
	}
	
	@Override
	public Runnable onTagRelationDelete(final String upperTagName, final String lowerTagName, final String userName, final Transaction session) {
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
}