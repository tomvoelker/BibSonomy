package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * TODO implement compartible method for concept structure
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexByTagNames extends BibTexChainElement {

	/**
	 * Returns a list of posts (bibtex) tagged with the given tags. Following
	 * arguments have to be given:
	 * 
	 * grouping:all name:irrelevant tags:given hash:null popular:false
	 * added:false
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final Transaction session) {
		log.debug(this.getClass().getSimpleName());

		List<Post<BibTex>> posts;
		if (param.getTagIndex().size() == 0) {
			log.debug("-> getBibTexForHomePage");
			posts = db.getBibTexForHomePage(param, session);
		} else {
			log.debug("-> getBibTexByTagNames");
			posts = db.getBibTexByTagNames(param, session);
		}
		return posts;
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return param.getGrouping() == GroupingEntity.ALL && param.getTagIndex() != null && param.getHash() == null && param.isPopular() == false && param.isAdded() == false;
	}
}