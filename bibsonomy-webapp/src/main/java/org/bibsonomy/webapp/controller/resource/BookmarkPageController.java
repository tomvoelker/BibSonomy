package org.bibsonomy.webapp.controller.resource;

import org.bibsonomy.model.Bookmark;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author dzo
 * @version $Id$
 */
public class BookmarkPageController extends AbstractResourcePageController<Bookmark> {

	@Override
	protected View getResourcePage() {
		return Views.URLPAGE;
	}

	@Override
	protected View getDetailsView() {
		// TODO redirect to url?!
		return null;
	}

	@Override
	protected Class<Bookmark> getResourceClass() {
		return Bookmark.class;
	}
}
