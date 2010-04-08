package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;
/**
 * @author mwa
 * @version $Id$
 */
public class GetRelatedTagsByAuthorAndTags extends TagChainElement{
	
	@Override
	protected List<Tag> handle(TagParam param, DBSession session) {

		if (this.db.isDoLuceneSearch()) {
			// FIXME: which parameters do we actually need?
			return this.db.getTagsByAuthorLucene(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), null, null, null, param.getSimHash(), extractTagNames(param.getTagIndex()), session);
		}
		
		return this.db.getRelatedTagsByAuthorAndTag(param, session);
	}

	@Override
	protected boolean canHandle(TagParam param) {
		return (SearchEntity.AUTHOR.equals(param.getSearchEntity())) &&
		   present(param.getTagIndex()) &&
		   !present(param.getBibtexKey()) &&
		   present(param.getSearch());
	}
	
	/**
	 * extracts list of tag names from given list of TagIndex instances
	 * 
	 * TODO: could we fill and use Generic.tags instead? 
	 * 
	 * @param tagIndex
	 * @return
	 */
	private List<String> extractTagNames(final List<TagIndex> tagIndex) {
		List<String> retVal = new LinkedList<String>();
		
		for( TagIndex tagIdx : tagIndex ) {
			retVal.add(tagIdx.getTagName());
		}
		
		return retVal;
	}
}
