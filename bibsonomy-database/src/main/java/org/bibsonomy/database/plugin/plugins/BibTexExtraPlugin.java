package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.BasketDatabaseManager;
import org.bibsonomy.database.managers.BibTexExtraDatabaseManager;
import org.bibsonomy.database.managers.DocumentDatabaseManager;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;

/**
 * This plugin takes care of additional features for BibTex posts.
 * 
 * XXX: we can't have a static/singleton {@link BasketDatabaseManager} instance,
 * because we have a circular dependency (the manager contains the plugins ...) could
 * be fixed by configuration the registry by spring beans
 * 
 * @author Christian Schenk
 */
public class BibTexExtraPlugin extends AbstractDatabasePlugin {

	@Override
	public void onPublicationDelete(final int contentId, final DBSession session) {
		final BibTexExtraDatabaseManager bibtexExtraDb = BibTexExtraDatabaseManager.getInstance();
		
		//delete related documents
		final DocumentDatabaseManager documentsManager = DocumentDatabaseManager.getInstance();
		documentsManager.deleteAllDocumentsForPost(contentId, session);
		
		// Delete id in extended fields table
		bibtexExtraDb.deleteAllExtendedFieldsData(contentId, session);
		// Delete id in bibtexturl table
		bibtexExtraDb.deleteAllURLs(contentId, session);
	}

	@Override
	public void onPublicationUpdate(final int newContentId, final int contentId, final DBSession session) {
		final BibTexExtraDatabaseManager bibtexExtraDb = BibTexExtraDatabaseManager.getInstance();
		bibtexExtraDb.updateURL(contentId, newContentId, session);
		bibtexExtraDb.updateDocument(contentId, newContentId, session);
		bibtexExtraDb.updateExtendedFieldsData(contentId, newContentId, session);
	}
}