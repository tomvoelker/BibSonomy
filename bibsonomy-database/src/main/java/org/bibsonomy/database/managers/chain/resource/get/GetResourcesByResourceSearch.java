package org.bibsonomy.database.managers.chain.resource.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.chain.resource.ResourceChainElement;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByResourceSearch;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author claus
 * @version $Id$
 * @param <R> the resource
 * @param <P> the param
 */
public abstract class GetResourcesByResourceSearch<R extends Resource, P extends ResourceParam<R>> extends ResourceChainElement<R, P> {

	@Override
	protected List<Post<R>> handle(P param, DBSession session) {
		// convert tag index to tag list
		List<String> tagIndex = null;
		if (present(param.getTagIndex())) {
			tagIndex = this.extractTagNames(param.getTagIndex());
		}
		
		// query the resource searcher
		return this.getDatabaseManagerForType(param.getClass()).getPostsByResourceSearch(
				param.getUserName(), param.getRequestedUserName(), param.getRequestedGroupName(), 
				param.getGroupNames(), param.getRawSearch(), param.getTitle(), param.getAuthor(), tagIndex, 
				null, null, null, 
				param.getLimit(), param.getOffset());
	}
	
	/**
	 * extracts list of tag names from given list of TagIndex instances
	 * 
	 * TODO: could we fill and use Generic.tags instead?
	 * TODO: @see {@link GetTagsByResourceSearch#extractTagNames}
	 * 
	 * @param tagIndex
	 * @return
	 */
	private List<String> extractTagNames(final List<TagIndex> tagIndex) {
		List<String> retVal = new LinkedList<String>();
		
		if (present(tagIndex)) {
			for( TagIndex tagIdx : tagIndex ) {
				retVal.add(tagIdx.getTagName());
			}
		}
		
		return retVal;
	}

}
