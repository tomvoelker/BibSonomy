/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.User;
import org.bibsonomy.util.HashUtils;

/**
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class UserUtils {

	
	/** Checks, if the given user is the special DBLP user 
	 * (which has some special rights).
	 *  
	 * @param user
	 * @return <code>true</code>, if <code>user</code> is the DBLP user.
	 */
	public static boolean isDBLPUser(final User user) {
		return isDBLPUser(user.getName());
	}
	
	
	/** Checks, if the given user name is the special DBLP user 
	 * (which has some special rights).
	 *  
	 * @param userName - the name of the user in question.
	 * @return <code>true</code>, if <code>user</code> is the DBLP user.
	 */
	public static boolean isDBLPUser(final String userName) {
		return "dblp".equalsIgnoreCase(userName);
	}
	
	/**
	 * Generates an Api key with a MD5 message digest from a random number.
	 * 
	 * @return String Api key
	 */
	public static String generateApiKey() {
		return HashUtils.getMD5Hash(generateRandom());
	}

	private static byte[] generateRandom() {
		final byte[] randomBytes = new byte[32];
		try {
			new Random().nextBytes(randomBytes);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		return randomBytes;
	}


	/**
	 * Helper function to set a user's groups by a list of group IDs
	 * 
	 * @param user
	 * @param groupIDs
	 */
	public static void setGroupsByGroupIDs(final User user, final List<Integer> groupIDs) {
		for (final int groupID : groupIDs) {
			user.addGroup(new Group(groupID));
		}
	}

	/**
	 * Helper function to get a list of group IDs from the user's list of groups
	 * 
	 * @param user
	 * @return list of groupIDs extracted from the given user's list of groups
	 */
	public static List<Integer> getListOfGroupIDs(final User user) {
		final ArrayList<Integer> groupIDs = new ArrayList<Integer>();
		final List<Group> groups = getListOfGroups(user);
		for (final Group group : groups) {
			groupIDs.add(group.getGroupId());
		}
		return groupIDs;
	}
	
	/**
	 * Helper function to get a list of groups from the user's list of groups
	 * 
	 * @param user
	 * @return list of groups extracted from the given user's list of groups
	 */
	public static List<Group> getListOfGroups(final User user) {
		final ArrayList<Group> groups = new ArrayList<Group>();
		/*
		 * every user may see public posts
		 */ 
		groups.add(new Group(GroupID.PUBLIC));
		if (user == null) {
			return groups;
		}
		groups.addAll(user.getGroups());
		return groups;
	}	
	
	/**
	 * Normalizes the OpenID of a user for matching
	 * 
	 * @param url the OpenID url
	 * @return normalized OpenID
	 */
	public static String normalizeURL(String url) {
		
		/*
		 * do nothing if url is empty
		 */
		if (url == null || url == "") {
			return url;
		}
		
		/*
		 * remove leading and trailing whitespaces
		 */
		url = url.trim();
		
		/*
		 * append http suffix if not set
		 */
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		
		/*
		 * append last backslash if not exist
		 */
		if (!url.endsWith("/")) {
			url += "/";
		}
		
		/*
		 * convert to lower case
		 */
		url = url.toLowerCase();
		
		return url;
	}
}