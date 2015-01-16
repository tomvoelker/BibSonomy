/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

package org.bibsonomy.rest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bibsonomy.model.enums.GoldStandardRelation;


/**
 * DO NOT CHANGE any constant values after a release
 * 
 * @author dzo
 */
public final class RESTConfig {
	private RESTConfig() {
		// noop
	}
	
	private static final String DATE_FORMAT_STRING = "yyyy-MM-DD HH:mm:ss";
	

	public static final String POSTS_URL = "posts";

	public static final String POSTS_ADDED_SUB_PATH = "added";

	public static final String POSTS_ADDED_URL = POSTS_URL + "/" + POSTS_ADDED_SUB_PATH;

	public static final String POSTS_POPULAR_SUB_PATH = "popular";

	public static final String POSTS_POPULAR_URL = POSTS_URL + "/" + POSTS_POPULAR_SUB_PATH;

	public static final String COMMUNITY_SUB_PATH = "community";

	public static final String API_USER_AGENT = "BibSonomyWebServiceClient";

	public static final String SYNC_URL = "sync";

	public static final String CONCEPTS_URL = "concepts";

	public static final String TAGS_URL = "tags";
	
	public static final String RELATION_PARAM = "relation";

	public static final String REFERENCES_SUB_PATH = "references";
	
	public static final String RELATION_REFERENCE = GoldStandardRelation.REFERENCE.toString().toLowerCase();

	public static final String RELATION_PARTOF = GoldStandardRelation.PART_OF.toString().toLowerCase();

	public static final String USERS_URL = "users";

	public static final String DOCUMENTS_SUB_PATH = "documents";

	public static final String FRIENDS_SUB_PATH = "friends";

	public static final String FOLLOWERS_SUB_PATH = "followers";

	public static final String GROUPS_URL = "groups";

	public static final String RESOURCE_TYPE_PARAM = "resourcetype";

	public static final String RESOURCE_PARAM = "resource";

	public static final String TAGS_PARAM = "tags";

	public static final String FILTER_PARAM = "filter";

	public static final String ORDER_PARAM = "order";
	
	public static final String SORTKEY_PARAM = "sortPage";
	
	public static final String SORTORDER_PARAM = "sortOrder";

	public static final String CONCEPT_STATUS_PARAM = "status";

	public static final String SEARCH_PARAM = "search";

	public static final String SUB_TAG_PARAM = "subtag";

	public static final String REGEX_PARAM = FILTER_PARAM;

	public static final String START_PARAM = "start";

	public static final String END_PARAM = "end";

	public static final String SYNC_STRATEGY_PARAM = "strategy";

	public static final String SYNC_DIRECTION_PARAM = "direction";

	public static final String SYNC_DATE_PARAM = "date";

	public static final String SYNC_STATUS = "status";

	public static final String CLIPBOARD_SUBSTRING = "clipboard";

	public static final String CLIPBOARD_CLEAR = "clear";

	/**
	 * Request Attribute ?relation="incoming/outgoing"
	 */
	public static final String ATTRIBUTE_KEY_RELATION = "relation";

	/** value for "incoming" */
	public static final String INCOMING_ATTRIBUTE_VALUE_RELATION = "incoming";

	/** value for "outgoing" */
	public static final String OUTGOING_ATTRIBUTE_VALUE_RELATION = "outgoing";

	/** default value */
	public static final String DEFAULT_ATTRIBUTE_VALUE_RELATION = INCOMING_ATTRIBUTE_VALUE_RELATION;

	/** place holder for the login user - used e.g. for OAuth requests */
	public static final String USER_ME = "@me";

	public static String serializeDate(final Date date) {
		final DateFormat fmt = new SimpleDateFormat(RESTConfig.DATE_FORMAT_STRING);
		return fmt.format(date);
	}

	public static Date parseDate(final String dateString) throws ParseException {
		final DateFormat fmt = new SimpleDateFormat(RESTConfig.DATE_FORMAT_STRING);
		return fmt.parse(dateString);
	}
}
