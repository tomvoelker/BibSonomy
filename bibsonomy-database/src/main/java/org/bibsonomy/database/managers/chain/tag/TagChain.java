package org.bibsonomy.database.managers.chain.tag;

import org.bibsonomy.database.managers.chain.ChainElementForTag;
import org.bibsonomy.database.managers.chain.FirstChainElementforTag;
import org.bibsonomy.database.managers.chain.tag.get.GetAllTags;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByExpression;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByGroup;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByGrouping;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByUser;

/**
 * @author mgr
 */

public class TagChain implements FirstChainElementforTag {
	
	private final ChainElementForTag getTagsByUser;
	private final ChainElementForTag getTagsByGroup;
	private final ChainElementForTag getTagsByGrouping;
	private final ChainElementForTag getTagsByRegularExpression;
	private final ChainElementForTag getAllTags;
	

	public TagChain() {
		
		this.getTagsByUser=new GetTagsByUser();
		this.getTagsByGroup=new GetTagsByGroup();
		this.getTagsByGrouping=new GetTagsByGrouping();
		this.getTagsByRegularExpression=new GetTagsByExpression();
		this.getAllTags=new GetAllTags();

		this.getTagsByUser.setNext(this.getTagsByGroup);
		this.getTagsByGroup.setNext(this.getTagsByGrouping);
		this.getTagsByGrouping.setNext(this.getTagsByRegularExpression);
		this.getTagsByRegularExpression.setNext(this.getAllTags);
	}

	public ChainElementForTag getFirstElementForTag() {
		return this.getTagsByUser;
	}
}