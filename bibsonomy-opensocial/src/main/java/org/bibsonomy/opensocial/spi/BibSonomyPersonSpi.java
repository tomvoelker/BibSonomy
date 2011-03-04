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
import org.bibsonomy.database.DBLogic;
import org.bibsonomy.database.DBLogicNoAuthInterfaceFactory;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;
import org.bibsonomy.database.util.IbatisDBSessionFactory;
import org.w3c.dom.UserDataHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

/**
 * Mock implementation of {@link PersonService}.
 *
 */
public class BibSonomyPersonSpi implements PersonService {

	/** The Constant JOHN. */
	private static final UserId JOHN = new UserId(UserId.Type.userId, "john.doe");

	/** The Constant JANE. */
	private static final UserId JANE = new UserId(UserId.Type.userId, "jane.doe");

	/** The Constant FRIENDS. */
	private static final UserId[] FRIENDS = {JOHN, JANE};

	/** mockup login user */
	private static final String LOGIN_USER     = "folke";
	private static final String LOGIN_PASSWORD = "passwd";

	private User loginUser;

	private DBSessionFactory dbSessionFactory;
	private DBLogicNoAuthInterfaceFactory dbLogicFactory;

	public BibSonomyPersonSpi() {
		loginUser = new User(LOGIN_USER);
	}
	
	public void init() {
		this.dbLogicFactory.setDbSessionFactory(this.getDbSessionFactory());
	}

	/* (non-Javadoc)
	 * @see org.apache.shindig.social.opensocial.spi.PersonService#getPerson(org.apache.shindig.social.opensocial.spi.UserId, java.util.Set, org.apache.shindig.auth.SecurityToken)
	 */
	public Future<Person> getPerson(UserId userId, Set<String> fields, SecurityToken token) throws ProtocolException {
		Person person = makePerson(getBibSonomyUser(userId));
		return ImmediateFuture.newInstance(person);
	}

	private User getBibSonomyUser(UserId userId) {
		LogicInterface dbLogic = getDbLogic();

		User dbUser;
		if( UserId.Type.userId.equals(userId.getType()) ) {
			dbUser = dbLogic.getUserDetails(userId.getUserId());
		} else {
			dbUser = dbLogic.getUserDetails(LOGIN_USER);
		}

		return dbUser;
	}

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
	
	private List<User> getBibSonomyFriends(Set<UserId> userIds, GroupId groupId) {
		LogicInterface dbLogic = getDbLogic();

		List<User> friends = new LinkedList<User>(); 
		for( UserId user : userIds ) {
			User dbUser = getBibSonomyUser(user);
			List<User> dbFriends = dbLogic.getUserRelationship(dbUser.getName(), UserRelation.OF_FRIEND);
			friends.addAll(dbFriends);
		}
		
		// finally get details for each friend
		List<User> retVal = new LinkedList<User>(); 
		for( User user : friends ) {
			User dbUser = dbLogic.getUserDetails(user.getName());
			retVal.add(dbUser);
		}
		
		return retVal;
	}

	private LogicInterface getDbLogic() {
		LogicInterface dbLogic = this.getDbLogicFactory().getLogicAccess(LOGIN_USER, LOGIN_PASSWORD);
		return dbLogic;
	}


	/* (non-Javadoc)
	 * @see org.apache.shindig.social.opensocial.spi.PersonService#getPeople(java.util.Set, org.apache.shindig.social.opensocial.spi.GroupId, org.apache.shindig.social.opensocial.spi.CollectionOptions, java.util.Set, org.apache.shindig.auth.SecurityToken)
	 */
	public Future<RestfulCollection<Person>> getPeople(Set<UserId> userIds, GroupId groupId,
			CollectionOptions collectionOptions, Set<String> fields, SecurityToken token)
			throws ProtocolException {
		try {
			List<Person> people = new ArrayList<Person>();
			switch (groupId.getType()) {
			case self:
				for (UserId userId: userIds) {
					Person person = new PersonImpl();
					person.setId(userId.getUserId());
					person.setEthnicity("youDontKnow");
					person.setAboutMe("Test");
					people.add(person);
				}
				break;
			case friends:
				List<User> friends = getBibSonomyFriends(userIds, groupId);
				for (User user: friends) {
					Person person = makePerson(user);
					people.add(person);
				}
				break;
			case all:
				//throw new SocialSpiException(ResponseError.NOT_IMPLEMENTED, "Not yet implemented",null);
				throw new ProtocolException(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not yet implemented");
			case groupId:
				//throw new SocialSpiException(ResponseError.NOT_IMPLEMENTED, "Not yet implemented",null);
				throw new ProtocolException(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not yet implemented");
			case deleted:
				//throw new SocialSpiException(ResponseError.NOT_IMPLEMENTED, "Not yet implemented",null);
				throw new ProtocolException(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not yet implemented");
			default:
				//throw new SocialSpiException(ResponseError.BAD_REQUEST, "Group ID not recognized",null);
				throw new ProtocolException(HttpServletResponse.SC_BAD_REQUEST, "Group ID not recognized");
			}
			return ImmediateFuture.newInstance(new RestfulCollection<Person>(people, 0, people.size()));
		} catch (Exception e) {
			//throw new SocialSpiException(ResponseError.INTERNAL_ERROR, "Exception occurred", e);
			throw new ProtocolException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Exception occurred", e);
		}
	}


	public void setDbLogicFactory(DBLogicNoAuthInterfaceFactory dbLogicFactory) {
		this.dbLogicFactory = dbLogicFactory;
	}

	public DBLogicNoAuthInterfaceFactory getDbLogicFactory() {
		return dbLogicFactory;
	}

	public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}

	public DBSessionFactory getDbSessionFactory() {
		return dbSessionFactory;
	}

}
