package org.bibsonomy.database.managers.chain.tag;

import org.bibsonomy.database.managers.chain.ChainElementForTag;
import org.bibsonomy.database.managers.chain.FirstChainElementForTag;
import org.bibsonomy.database.managers.chain.tag.get.GetAllTags;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByExpression;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByGroup;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsViewable;
import org.bibsonomy.database.managers.chain.tag.get.GetTagsByUser;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class TagChain implements FirstChainElementForTag {

	private final ChainElementForTag getTagsByUser;
	private final ChainElementForTag getTagsByGroup;
	private final ChainElementForTag getTagsViewable;
	private final ChainElementForTag getTagsByRegularExpression;
	private final ChainElementForTag getAllTags;

	public TagChain() {
		this.getTagsByUser = new GetTagsByUser();
		this.getTagsByGroup = new GetTagsByGroup();
		this.getTagsViewable = new GetTagsViewable();
		this.getTagsByRegularExpression = new GetTagsByExpression();
		this.getAllTags = new GetAllTags();

		this.getTagsByUser.setNext(this.getTagsByGroup);
		this.getTagsByGroup.setNext(this.getTagsViewable);
		this.getTagsViewable.setNext(this.getTagsByRegularExpression);
		this.getTagsByRegularExpression.setNext(this.getAllTags);
	}

	public ChainElementForTag getFirstElementForTag() {
		return this.getTagsByUser;
	}
}