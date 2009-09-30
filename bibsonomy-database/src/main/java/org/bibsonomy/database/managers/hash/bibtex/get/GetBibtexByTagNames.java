package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.List;

import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's tagged with the given tags.
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexByTagNames extends BibTexHashElement {

	/**
	 * TODO : improve doc
	 */
	public GetBibtexByTagNames() {
		setTagIndex(true);
		setNumSimpleTagsOverNull(true);
		
		addToOrders(Order.FOLKRANK);
		addToOrders(Order.ADDED);
	}

	/**
	 * Returns a list of posts (bibtex) tagged with the given tags.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		return this.db.getBibTexByTagNames(param.getGroupId(), param.getTagIndex(), param.getLimit(), param.getOffset(), session);
	}
}