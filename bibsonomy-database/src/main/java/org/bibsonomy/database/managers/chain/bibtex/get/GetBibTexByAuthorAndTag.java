package org.bibsonomy.database.managers.chain.bibtex.get;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for given tag/tags and author.
 * 
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
@Deprecated // TODO: remove
public class GetBibTexByAuthorAndTag extends BibTexChainElement {
	private static final Log LOGGER = LogFactory.getLog(GetBibTexByAuthorAndTag.class);

//	@SuppressWarnings("deprecation") // TODO: lucene can't handle system tags
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		if (this.db.isDoLuceneSearch()) {
			LOGGER.debug("Using Lucene in GetBibtexByAuthor");
			
			// TODO: maybe a clone of GetRelatedTagsByAuthorAndTags#extractTagNames
			List<String> tagList = null;
			if ((null != param.getTagIndex()) && (!param.getTagIndex().isEmpty())) {
				tagList = new ArrayList<String>();
				for ( TagIndex tagIndex : param.getTagIndex()){
					tagList.add(tagIndex.getTagName());
				}
			}
			
			// TODO: lucene can't handle system tags
			return this.db.getPostsByAuthorLucene(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getYear(), 
					param.getFirstYear(), param.getLastYear(), param.getLimit(), param.getOffset(), param.getSimHash(), tagList, session);
		}
		
		return this.db.getPostsByAuthorAndTag(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), param.getTagIndex(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return false;
	}
}