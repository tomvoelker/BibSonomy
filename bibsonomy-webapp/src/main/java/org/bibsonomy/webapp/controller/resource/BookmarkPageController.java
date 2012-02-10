package org.bibsonomy.webapp.controller.resource;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.GoldStandardBookmark;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author dzo
 * @version $Id$
 */
public class BookmarkPageController extends AbstractResourcePageController<Bookmark, GoldStandardBookmark> {
	
	@Override
	protected View getResourcePage() {
		return Views.URLPAGE;
	}

	@Override
	protected View getDetailsView() {
		return this.getResourcePage();
	}

	@Override
	protected Class<Bookmark> getResourceClass() {
		return Bookmark.class;
	}
	
	@Override
	protected Class<GoldStandardBookmark> getGoldStandardClass() {
		return GoldStandardBookmark.class;
	}
}
