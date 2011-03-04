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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Mock implementation of {@link PersonService}.
 *
 */
public class MockPersonSpi implements PersonService {

  /** The Constant JOHN. */
  private static final UserId JOHN = new UserId(UserId.Type.userId, "john.doe");

  /** The Constant JANE. */
  private static final UserId JANE = new UserId(UserId.Type.userId, "jane.doe");

  /** The Constant FRIENDS. */
  private static final UserId[] FRIENDS = {JOHN, JANE};
  
  HttpServletRequest req;

  /* (non-Javadoc)
   * @see org.apache.shindig.social.opensocial.spi.PersonService#getPerson(org.apache.shindig.social.opensocial.spi.UserId, java.util.Set, org.apache.shindig.auth.SecurityToken)
   */
  public Future<Person> getPerson(UserId userId, Set<String> fields, SecurityToken token)
    throws ProtocolException {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
  
    Person person = new PersonImpl();
    person.setId(userId.getUserId());
    person.setEthnicity("youDontKnow");
    person.setAboutMe("Test");
    person.setDisplayName(userId.getUserId());
    return ImmediateFuture.newInstance(person);
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
          person.setDisplayName(userId.getUserId());
          person.setEthnicity("youDontKnow");
          person.setAboutMe("Test");
          people.add(person);
        }
        break;
      case friends:
        for (UserId userId: FRIENDS) {
          Person person = new PersonImpl();
          person.setId(userId.getUserId());
          person.setDisplayName(userId.getUserId());
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

}
