package org.bibsonomy.wiki.tags;

import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.common.enums.GroupingEntity;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Used for rending tags which are available for both users and groups, i.e. shared tags.
 * 
 * @author Bernd Terbrack
 * @version $Id$
 */
public abstract class SharedTag extends AbstractTag {
	
	public static final String EMPTY_NAME = "";

	/**
	 * Create new shared tag.
	 * 
	 * @param name the name of the tag
	 */
	public SharedTag(final String name) {
		super(name);
	}
	

	@Override
	protected String renderSafe() {
		return this.renderSharedTag();

	}
	
	/**
	 * return the currently requested grouping entity - i.e. 
	 *   GroupingEntity.USER if we're working on a user, and
	 *   GroupingEntity.GROUP when working on a group.
	 *   
	 * @return - the corresponding grouping entity.
	 */
	protected GroupingEntity getGroupingEntity() {
		// If the group is "null" then its a user (obviously)
		return this.requestedGroup != null ? GroupingEntity.GROUP : GroupingEntity.USER;
	}
	
	
	
	/**
	 * Return the realname of the requested user / group.
	 * 
	 * If the requesting user isn't allowed to see the real name of the requested
	 * user / group, we return the ordinary username instead.
	 */
	protected String getRequestedRealName() {
		switch (getGroupingEntity()) {
		case USER:
			return Utils.escapeXmlChars( present(this.requestedUser.getRealname()) ? this.requestedUser.getRealname() : this.requestedUser.getName() );
		case GROUP:
			return Utils.escapeXmlChars( present(this.requestedGroup.getRealname()) ? this.requestedGroup.getRealname() : this.requestedGroup.getName() );
		default: 
			return EMPTY_NAME;	
		}
	}
	
	/**
	 * Return the name of the requested user / group.
	 * @return The name of the User/Group based on the requestType
	 */
	protected String getRequestedName() {
		switch (getGroupingEntity()) {
		case USER:
			return Utils.escapeXmlChars(this.requestedUser.getName());
		case GROUP:
			return Utils.escapeXmlChars(this.requestedGroup.getName());
		default: 
			return EMPTY_NAME;	
		}
	}

	protected abstract String renderSharedTag();

}
