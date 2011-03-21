package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

import static org.bibsonomy.rest.strategy.users.GetRelatedusersForUserStrategy.chooseRelationship;
import static org.bibsonomy.rest.strategy.users.GetRelatedusersForUserStrategy.chooseRelation;

/**
 * @author dbe
 * @version $Id$
 */
public class PostRelatedusersForUserStrategy extends AbstractCreateStrategy {
	
	private String userName;
	private String relation;
	private String tag;
	private UserRelation relationship;
	
	public PostRelatedusersForUserStrategy(Context context, final String userName, final String relationship, String tag) {
		super(context);
		this.userName = userName;
		this.tag = tag;
		this.relation = chooseRelation(context);
		this.relationship = chooseRelationship(relationship, this.relation);
	}

	@Override
	protected void render(Writer writer, String resourceID) {
		this.getRenderer().serializeUserId(writer, resourceID);
	}

	@Override
	protected String create() {
		User targetUser = parseUser();
		try {
			this.getLogic().createUserRelationship(this.userName, targetUser.getName(), relationship, null);
		}
		catch (DatabaseException de) {
			//FIXME: handle exceptions
		}
		/*
		 * we return the userName as "resource id" to be sent back in the
		 * response in case of success 
		 */
		return this.userName;
	}
	
	private User parseUser() {
		return this.getRenderer().parseUser(this.doc);		
	}

}
