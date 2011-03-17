/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.bibsonomy.opensocial.spi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.core.model.PersonImpl;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.ShindigLogicInterfaceFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * Mock implementation of {@link PersonService}.
 *
 */
public class BibSonomyPersonSpi implements PersonService {

	/** logic factory for accessing BibSonomy data */
	private ShindigLogicInterfaceFactory dbLogicFactory;

	//------------------------------------------------------------------------
	// PersonService interface
	//------------------------------------------------------------------------
	/**
	 * Returns a person that corresponds to the passed in person id.
	 * 
	 * @see org.apache.shindig.social.opensocial.spi.PersonService#getPerson
	 * 
	 * @param id The id of the person to fetch.
	 * @param fields The fields to fetch.
	 * @param token The gadget token
	 * @return a list of people.
	 */
	public Future<Person> getPerson(UserId userId, Set<String> fields, SecurityToken token) throws ProtocolException {
		LogicInterface dbLogic = this.dbLogicFactory.getLogicAccess(token);
		Person person = makePerson(getBibSonomyUser(userId, dbLogic, token));
		return ImmediateFuture.newInstance(person);
	}


	/**
	 * Returns a list of people that correspond to the passed in person ids.
	 *
	 * @see org.apache.shindig.social.opensocial.spi.PersonService#getPeople
	 *
	 * @param userIds A set of users
	 * @param groupId The group
	 * @param collectionOptions How to filter, sort and paginate the collection being fetched
	 * @param fields The profile details to fetch. Empty set implies all
	 * @param token The gadget token @return a list of people.
	 * @return Future that returns a RestfulCollection of Person
	 */
	public Future<RestfulCollection<Person>> getPeople(Set<UserId> userIds, GroupId groupId, CollectionOptions collectionOptions, Set<String> fields, SecurityToken token) throws ProtocolException {
		LogicInterface dbLogic = this.dbLogicFactory.getLogicAccess(token);
		try {
			List<Person> people = new ArrayList<Person>();
			switch (groupId.getType()) {
			case self:
				//
				// get user profiles for given list of users
				//
				for (UserId userId: userIds) {
					User dbUser = getBibSonomyUser(userId, dbLogic, token);
					Person person = makePerson(dbUser);
					people.add(person);
				}
				break;
			case friends:
				//
				// get all friends for given list of users
				//
				List<User> friends = getBibSonomyFriends(userIds, groupId, dbLogic, token);
				for (User user: friends) {
					Person person = makePerson(user);
					people.add(person);
				}
				break;
			case all:
				//
				// get all users connected to given users
				//
				throw new ProtocolException(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not yet implemented");
			case groupId:
				//
				// get all users belonging to a given group
				//
				throw new ProtocolException(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not yet implemented");
			case deleted:
				throw new ProtocolException(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not yet implemented");
			default:
				throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST, "Group ID not recognized");
			}
			return ImmediateFuture.newInstance(new RestfulCollection<Person>(people, 0, people.size()));
		} catch (Exception e) {
			throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Exception occurred", e);
		}
	}

	//------------------------------------------------------------------------
	// private helper
	//------------------------------------------------------------------------
	/**
	 * get BibSonomy user object for given user id
	 * 
	 * @param userId
	 * @param dbLogic
	 * @param token
	 * @return
	 */
	private User getBibSonomyUser(UserId userId, LogicInterface dbLogic, SecurityToken token) {
		String userName = getUserName(userId, token);
		
		User dbUser = dbLogic.getUserDetails(userName);

		return dbUser;
	}

	/**
	 * get all BibSonomy friends for the requested users
	 * 
	 * @param userIds
	 * @param groupId
	 * @param dbLogic
	 * @param token
	 * @return
	 */
	private List<User> getBibSonomyFriends(Set<UserId> userIds, GroupId groupId, LogicInterface dbLogic, SecurityToken token) {
		
		List<User> friends = new LinkedList<User>(); 
		for( UserId user : userIds ) {
			String userName = getUserName(user, token);
			List<User> dbFriends = dbLogic.getUserRelationship(userName, UserRelation.OF_FRIEND);
			friends.addAll(dbFriends);
		}

		// finally get details for each friend
		/*
		List<User> retVal = new LinkedList<User>(); 
		for( User user : friends ) {
			User dbUser = dbLogic.getUserDetails(user.getName());
			retVal.add(dbUser);
		}
		
		return retVal;
		*/
		
		return friends;
	}
	
	/**
	 * convert a BibSonomy user to an OpenSocial user object
	 * 
	 * @param dbUser
	 * @return
	 */
	private Person makePerson(User dbUser) {
		Person person = new PersonImpl();
		person.setId(dbUser.getName());
		person.setThumbnailUrl("http://www.bibsonomy.org/picture/user/"+dbUser.getName());
		if( dbUser.getRealname()!=null) {
			person.setDisplayName(dbUser.getRealname());
		} else {
			person.setDisplayName(dbUser.getName());
		}
		return person;
	}

	/**
	 * returns user name to given request
	 * 
	 * @param user
	 * @param token
	 * @return
	 */
	private String getUserName(UserId userId, SecurityToken token) {
		String userName;
		
		switch (userId.getType()) {
		case viewer:
			userName = token.getViewerId();
			break;
		case userId:
			userName = userId.getUserId();
			break;
		case me:
			userName = token.getViewerId();
			break;
		case owner:
			// FIXME: how do we handle this?
		default:
			throw new ProtocolException(HttpServletResponse.SC_NOT_IMPLEMENTED, "UserType '"+userId.getType().name()+"' not yet implemented");
		}
		
		return userName;
	}



	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setDbLogicFactory(ShindigLogicInterfaceFactory dbLogicFactory) {
		this.dbLogicFactory = dbLogicFactory;
	}

	public ShindigLogicInterfaceFactory getDbLogicFactory() {
		return dbLogicFactory;
	}

}
