package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Return all BibTex entries by the respective title.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibTexByTitle extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByTitle(param.getTitle(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				present(param.getTitle()) && 
				present(param.getLimit()) &&
				present(param.getOffset())
				);
	}
}