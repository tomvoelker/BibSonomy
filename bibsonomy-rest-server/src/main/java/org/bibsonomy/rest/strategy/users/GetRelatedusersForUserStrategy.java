/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * 
 * Gets related users for a given user (e.g. friends, followers).
 * 
 * @author ema, dbe
 */
public class GetRelatedusersForUserStrategy extends AbstractGetListStrategy<List<User>> {

	private String userName = null;
	private String relation = null;
	private UserRelation relationship = null;
	private String tag = null;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            - the context of the request
	 * @param userName
	 *            - the user name for whic related users should be queried
	 * @param relationship
	 *            - the kind of relationship to be queried
	 * @param tag TODO
	 */
	public GetRelatedusersForUserStrategy(final Context context, final String userName, final String relationship, final String tag) {
		super(context);
		this.userName = userName;
		this.tag = tag;
		this.relation = chooseRelation(context);
		this.relationship = chooseRelationship(relationship, this.relation);
	}

	@Override
	protected void render(final Writer writer, final List<User> resultList) {
		this.getRenderer().serializeUsers(writer, resultList, getView());

	}

	@Override
	protected List<User> getList() {
		return this.getLogic().getUserRelationship(userName, relationship, tag);
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(this.getUrlRenderer().getApiUrl() + RESTConfig.FRIENDS_SUB_PATH);
	}

	@Override
	protected void appendLinkPostFix(final StringBuilder sb) {
	}

	@Override
	protected String getContentType() {
		return "users";
	}

	/**
	 * Choose the right UserRelation enum, based on kind of relationship and the
	 * direction
	 * 
	 * @param relationship
	 *            - the kind of relationship
	 * @param relation
	 *            - the direction
	 * @return the appropriate UserRelatkion enum
	 */
	public static UserRelation chooseRelationship(final String relationship, final String relation) {
		if (RESTConfig.FRIENDS_SUB_PATH.equals(relationship)) {
			if (RESTConfig.OUTGOING_ATTRIBUTE_VALUE_RELATION.equals(relation)) {
				return UserRelation.FRIEND_OF;
			}
			return UserRelation.OF_FRIEND;
		} else if (RESTConfig.FOLLOWERS_SUB_PATH.equals(relationship)) {
			if (RESTConfig.OUTGOING_ATTRIBUTE_VALUE_RELATION.equals(relation)) {
				return UserRelation.FOLLOWER_OF;
			}
			return UserRelation.OF_FOLLOWER;
		}
		throw new NoSuchResourceException("No resources for relationship type " + relationship + " available - please check your URL syntax.");
	}

	/**
	 * Choose the approprate relation, based on the URL parameter.
	 * 
	 * @param context
	 *            - the context of the request
	 * @return - the appropriate relation.
	 */
	public static String chooseRelation(Context context) {
		String rel = context.getStringAttribute(RESTConfig.ATTRIBUTE_KEY_RELATION, RESTConfig.DEFAULT_ATTRIBUTE_VALUE_RELATION);
		if (!(RESTConfig.INCOMING_ATTRIBUTE_VALUE_RELATION.equals(rel) || RESTConfig.OUTGOING_ATTRIBUTE_VALUE_RELATION.equals(rel))) {
			return RESTConfig.DEFAULT_ATTRIBUTE_VALUE_RELATION;
		}
		return rel;
	}

}
