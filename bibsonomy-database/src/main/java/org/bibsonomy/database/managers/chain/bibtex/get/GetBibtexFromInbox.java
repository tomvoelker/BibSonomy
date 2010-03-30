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
 * Returns the posts in the users Inbox
 * 
 * @author sdo
 * @version $Id$
 */
public class GetBibtexFromInbox extends BibTexChainElement {
	
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		if (present(param.getHash())) {
			/*
			 * If an intraHash is given, we retrieve only the posts with this hash from the users inbox 
			 */
			return this.db.getPostsFromInboxByHash(param.getUserName(), param.getHash(), session);
		}
		/*
		 * return all posts from the users inbox
		 */
		return this.db.getPostsFromInbox(param.getUserName(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.INBOX);
	}


}
