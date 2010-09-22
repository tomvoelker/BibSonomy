package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given author.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByResourceSearch extends TagChainElement {
	
	
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		Collection<String> tags = null;
		if( present(param.getTagIndex()) )  {
			tags = DatabaseUtils.extractTagNames(param.getTagIndex());
		}
		return this.db.getTagsByResourceSearch(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getGroupNames(), param.getSearch(), param.getTitle(), param.getAuthor(), tags, null, null, null, param.getLimit(), param.getOffset());
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return ( !present(param.getBibtexKey()) &&
				 !present(param.getRegex()) &&
				 !present(param.getHash()) &&
				 !present(param.getTagRelationType()) &&
				(present(param.getSearch()) || present(param.getTitle()) || present(param.getAuthor())) );
	}	
}