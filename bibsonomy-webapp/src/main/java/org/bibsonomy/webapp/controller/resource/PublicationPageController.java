package org.bibsonomy.webapp.controller.resource;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author dzo
 * @version $Id$
 */
public class PublicationPageController extends AbstractResourcePageController<BibTex> {

	@Override
	protected View getResourcePage() {
		return Views.BIBTEXPAGE;
	}

	@Override
	protected View getDetailsView() {
		return Views.BIBTEXDETAILS;
	}

	@Override
	protected Class<BibTex> getResourceClass() {
		return BibTex.class;
	}

}
