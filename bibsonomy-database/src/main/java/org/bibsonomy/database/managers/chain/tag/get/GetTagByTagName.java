package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public class GetTagByTagName extends TagChainElement {

	/* 
	 * This class can handle the request if the tag name is set and no sub- or supertags are included
	 */
	@Override
	protected boolean canHandle(final TagParam param) {		
		return present(param.getTagName()) && !param.isRetrieveSubTags() && !param.isRetrieveSuperTags();
	}

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
//		final TagParam param = new TagParam();
//		param.setName(tagName);
//				
//		List<Tag> tags = db.getTagByTagName(param, session);
//		if (tags.size() != 0) {
//			System.out.println("GetTagByTagName");
//		}
//		return tags;
		return null;
	}
}
