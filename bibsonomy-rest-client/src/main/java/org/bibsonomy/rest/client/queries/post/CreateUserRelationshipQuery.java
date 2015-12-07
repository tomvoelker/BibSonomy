/**
 * BibSonomy-Rest-Client - The REST-client.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.client.queries.post;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.StringWriter;

import org.bibsonomy.model.User;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

/**
 * Create a relationship among users.
 * 
 * @author Dominik Benz, benz@cs.uni-kassel.de
 */
public class CreateUserRelationshipQuery extends AbstractQuery<String> {

	/** parameter value for friend relationship */
	public final static String FRIEND_RELATIONSHIP = "friend";
	/** paramaeter value for follower relationship */
	public final static String FOLLOWER_RELATIONSHIP = "follower";

	/** source user */
	private final String username;
	/** target user */
	private final String targetUserName;
	/** type of relationship (friend/follower) */
	private final String relationType;
	/** tag for tagged relationships */
	private final String tag;

	/**
	 * Create new query.
	 * 
	 * @param username
	 *            - the (currently logged in) source user
	 * @param targetUserName
	 *            - the name of the user to establish a relationship with
	 * @param relationType
	 *            - the type of relationship (i.e. "friend" or "follower"
	 * @param tag
	 *            - a tag (for taggged relationships)
	 */
	public CreateUserRelationshipQuery(final String username, final String targetUserName, final String relationType, final String tag) {
		/*
		 * check input
		 */
		if (!present(username)) throw new IllegalArgumentException("No source user given!");
		if (!present(targetUserName)) throw new IllegalArgumentException("No target user given");
		if (!(FRIEND_RELATIONSHIP.equals(relationType) || FOLLOWER_RELATIONSHIP.equals(relationType))) {
			throw new IllegalArgumentException("Relation type must be either '" + FRIEND_RELATIONSHIP + "' or '" + FOLLOWER_RELATIONSHIP + "'");
		}
		/*
		 * set params
		 */
		this.username = username;
		this.targetUserName = targetUserName;
		this.relationType = relationType;
		this.tag = tag;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		/*
		 * create body of request
		 */
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeUser(sw, new User(this.targetUserName), null);
		/* 
		 * TODO: move url generation to url renderer
		 * friend/follower, tag
		 */
		final String friendOrFollower = FRIEND_RELATIONSHIP.equals(relationType) ? RESTConfig.FRIENDS_SUB_PATH : RESTConfig.FOLLOWERS_SUB_PATH;
		final String queryTag = present(this.tag) ? "/" + tag : "";
		/*
		 * perform request
		 */
		// FIXME: document why we call StringUtils.toDefaultCharset twice
		this.downloadedDocument = performRequest(HttpMethod.POST, this.getUrlRenderer().getApiUrl() + RESTConfig.USERS_URL + "/" + this.username + "/" + friendOrFollower + queryTag, StringUtils.toDefaultCharset(StringUtils.toDefaultCharset(sw.toString())));
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.client.AbstractQuery#getResultInternal()
	 */
	@Override
	protected String getResultInternal() throws BadRequestOrResponseException, IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}
}
