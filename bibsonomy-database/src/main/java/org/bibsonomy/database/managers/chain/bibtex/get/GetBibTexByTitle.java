package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.List;

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
		/*
		{"items":
			[{
				"title":"test post schnolke",
				"year":"2009",
				"author":"jaschke",
				"entry_type":"article",
				"editor":"wir ihr sie sollte"
			}]
		}	 
		 */
		List<Post<BibTex>> posts = this.db.getPostsByTitleLucene(param.getTitle(), 0, null, param.getUserName(), param.getGroupNames(), param.getLimit(), param.getOffset(), session);
		return posts;
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return false;
	}
}