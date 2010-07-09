package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByResourceSearch;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByResourceSearch;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author fei
 * @version $Id$
 */
public class GetBibtexByResourceSearch extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		// convert tag index to tag list
		List<String> tagIndex = null;
		if (present(param.getTagIndex())) {
			tagIndex = extractTagNames(param.getTagIndex());
		}
		
		// query the resource searcher
		return this.db.getPostsByResourceSearch(param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(),  param.getGroupNames(), param.getRawSearch(), param.getTitle(), param.getAuthor(), tagIndex, param.getYear(), param.getFirstYear(), param.getLastYear(),  param.getLimit(), param.getOffset());
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (!present(param.getBibtexKey()) &&
				(present(param.getSearch()) || present(param.getAuthor()) || present(param.getTitle()))
				); 
	}
	
	/**
	 * extracts list of tag names from given list of TagIndex instances
	 * 
	 * TODO: could we fill and use Generic.tags instead? 
	 * TODO: code copy @see {@link GetBookmarksByResourceSearch#extractTagNames} and @see {@link GetTagsByResourceSearch#extractTagNames}
	 * 
	 * @param tagIndex
	 * @return
	 */
	private List<String> extractTagNames(final List<TagIndex> tagIndex) {
		final List<String> retVal = new LinkedList<String>();
		
		for (final TagIndex tagIdx : tagIndex) {
			retVal.add(tagIdx.getTagName());
		}
		
		return retVal;
	}	
}