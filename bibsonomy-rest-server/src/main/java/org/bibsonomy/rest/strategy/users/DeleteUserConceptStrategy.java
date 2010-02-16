package org.bibsonomy.rest.strategy.users;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public class DeleteUserConceptStrategy extends AbstractDeleteStrategy {

	private final String conceptName;
	private final String userName;
	private final String lowerTag;
	
	/**
	 * sets fields
	 * @param context
	 * @param conceptName
	 * @param userName
	 */
	public DeleteUserConceptStrategy(Context context, String conceptName, String userName) {
		super(context);
		this.conceptName = conceptName;
		this.userName = userName;
		
		this.lowerTag = context.getStringAttribute("subtag", null);		
	}

	@Override
	protected boolean delete() {
		// delete whole concept
		if (this.lowerTag == null) {
			this.getLogic().deleteConcept(this.conceptName, GroupingEntity.USER, this.userName);		
		} else {
			// delete relation only
			this.getLogic().deleteRelation(this.conceptName, this.lowerTag, GroupingEntity.USER, this.userName);
		}
		return true;
	}

	@Override
	protected String getContentType() {
		return null;
	}
}