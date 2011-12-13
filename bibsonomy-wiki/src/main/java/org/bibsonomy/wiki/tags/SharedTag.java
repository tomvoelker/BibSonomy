package org.bibsonomy.wiki.tags;

import info.bliki.htmlcleaner.Utils;

import org.bibsonomy.common.enums.GroupingEntity;

/**
 * 
 * @author Bernd
 * @version $Id$
 */
public abstract class SharedTag extends AbstractTag {

	/**
	 * @param name the name of the tag
	 */
	public SharedTag(final String name) {
		super(name);
	}
	
	@Deprecated // TODO: introduce supertype for user and group 
	protected enum RequestType {
		USER("user",GroupingEntity.USER),
		GROUP("group",GroupingEntity.GROUP);
		
		private RequestType(final String type,final GroupingEntity groupingEntity) {
			this.type = type;
			this.groupingEntity = groupingEntity;
		}
		
		/**
		 * @return the type
		 */
		public String getType() {
			return this.type;
		}
		
		/**
		 * @return the groupingEntity
		 */
		public GroupingEntity getGroupingEntity() {
			return this.groupingEntity;
		}

		private final String type;
		private final GroupingEntity groupingEntity;
		
	}

	@Override
	protected String renderSafe() {
		// If the group is "null" then its a user (obviously)
		return this.requestedGroup != null  ? this.renderSharedTag(RequestType.GROUP) : this.renderSharedTag(RequestType.USER);
	}
	
	
	
	/*
	 * TODO: if current user isn't allowed to see the real name of the requested
	 * user realname is null, return the name instead?
	 */
	protected String getRequestedRealName(final RequestType requestType) {
		switch (requestType) {
		case USER:
			return Utils.escapeXmlChars(this.requestedUser.getRealname());
		case GROUP:
			return Utils.escapeXmlChars(this.requestedGroup.getRealname());
		default: 
			return this.name = "";	
		}
	}
	
	/**
	 * @param requestType
	 * @return The name of the User/Group based on the requestType
	 */
	protected String getRequestedName(final RequestType requestType) {
		switch (requestType) {
		case USER:
			return Utils.escapeXmlChars(this.requestedUser.getName());
		case GROUP:
			return Utils.escapeXmlChars(this.requestedGroup.getName());
		default: 
			return this.name = "";	
		}
	}

	protected abstract String renderSharedTag(RequestType requestType);

}
