package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for given author.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
@Deprecated // TODO: remove!
public class GetBibTexByAuthor extends BibTexChainElement {

	// TODO: lucene can't handle system tags
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		if (this.db.isDoLuceneSearch()) {
			/*
			 * FIXME: why is the parameter "tagIndex" = null? 
			 * TODO: lucene can't handle system tags
			 */
			log.debug("Using Lucene in GetBibtexByAuthor");
			return this.db.getPostsByAuthorLucene(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getYear(), 
					param.getFirstYear(), param.getLastYear(), param.getLimit(), param.getOffset(), param.getSimHash(), null, session);
		}
		
		return this.db.getPostsByAuthor(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	
	@Override
	protected boolean canHandle(final BibTexParam param) {
		return false;
	}	
}