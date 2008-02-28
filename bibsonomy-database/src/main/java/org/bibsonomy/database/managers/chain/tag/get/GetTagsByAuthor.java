package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

	/**
	 * @author miranda 
	 */
public class GetTagsByAuthor extends TagChainElement{

	
	/**
	 * return a list of tags by a given author
	 */
	
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		return this.db.getTagsAuthor(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return present(param.getGrouping()== GroupingEntity.VIEWABLE) && 
		       present(param.getSearch());
	}
	
	
}
