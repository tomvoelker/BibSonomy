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
package org.apache.shindig.samples.springexample.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.testing.FakeGadgetToken;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.bibsonomy.opensocial.spi.MockPersonSpi;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test that MockPersonSpi works as expected
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-context.xml"})
@Ignore
public class PersonSpiTest {

  // What's being tested
  /** The person spi. */
  @Autowired
  private MockPersonSpi personSpi;

  /** The Constant CANONICAL_USERNAME. */
  private static final String CANONICAL_USERNAME = "canonical";

  /** The Constant JOHN_USERNAME. */
  private static final String JOHN_USERNAME = "john.doe";

  /** The Constant JANE_USERNAME. */
  private static final String JANE_USERNAME = "jane.doe";

  /** The CANONICA l_ user. */
  private final UserId CANONICAL_USER = new UserId(UserId.Type.userId, CANONICAL_USERNAME);

  /** The token. */
  private final SecurityToken token = new FakeGadgetToken();

  /**
   * Should return expected canonical person.
   *
   * @throws Exception the exception
   */
  @Test
  public void shouldReturnCanonicalPerson() throws Exception {
    Future<Person> person = personSpi.getPerson(CANONICAL_USER, Person.Field.DEFAULT_FIELDS, token);
    assertNotNull(person);
    assertNotNull(person.get());
    assertEquals(CANONICAL_USERNAME, person.get().getId());
  }

  /**
   * Should return expected canonical person.
   *
   * @throws Exception the exception
   */
  @Test
  public void shouldReturnSelf() throws Exception {
    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(CANONICAL_USER);
    Future<RestfulCollection<Person>> people = personSpi.getPeople(userIds,
            new GroupId(GroupId.Type.self, null), null, Person.Field.DEFAULT_FIELDS, token);
    assertNotNull(people);
    assertNotNull(people.get());
    assertEquals(1, people.get().getTotalResults());
    assertEquals(CANONICAL_USERNAME, people.get().getEntry().get(0).getId());
  }

  /**
   * Should return expected friends.
   *
   * @throws Exception the exception
   */
  @Test
  public void shouldReturnFriends() throws Exception {
    Set<UserId> userIds = new HashSet<UserId>();
    userIds.add(CANONICAL_USER);
    Future<RestfulCollection<Person>> people = personSpi.getPeople(userIds,
            new GroupId(GroupId.Type.friends, null), null, Person.Field.DEFAULT_FIELDS, token);
    assertNotNull(people);
    assertNotNull(people.get());
    assertEquals(2, people.get().getTotalResults());
    assertEquals(JOHN_USERNAME, people.get().getEntry().get(0).getId());
    assertEquals(JANE_USERNAME, people.get().getEntry().get(1).getId());
  }

}
