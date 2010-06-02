package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given hash.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexByHash extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		return this.db.getPostsByHash(param.getHash(), HashID.getSimHash(param.getSimHash()), GroupID.PUBLIC.getId(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (present(param.getHash()) &&
				!present(param.getBibtexKey()) &&
				param.getGrouping() == GroupingEntity.ALL &&
				!present(param.getRequestedUserName()) &&
				!present(param.getTagIndex()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()));
	}
}