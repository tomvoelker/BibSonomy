/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.testutil.ModelUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Christian Schenk
 */
public class UserTest {

	/**
	 * tests addGroup
	 */
	@Test
	public void addGroup() {
		User user = new User();
		assertEquals(0, user.getGroups().size());
		user.addGroup(new Group());
		user.addGroup(new Group());
		assertEquals(2, user.getGroups().size());

		// don't call getGroups before addGroup
		user = new User();
		user.addGroup(new Group());
		user.addGroup(new Group());
		assertEquals(2, user.getGroups().size());
	}

	/**
	 * tests addFriends
	 */
	@Test
	public void addFriends() {
		User user = new User();
		assertEquals(0, user.getFriends().size());
		user.addFriend(new User());
		user.addFriend(new User());
		assertEquals(2, user.getFriends().size());
		List<User> friendsList = new ArrayList<User>();
		friendsList.add(new User());
		friendsList.add(new User());
		user.addFriends(friendsList);
		assertEquals(4, user.getFriends().size());

		// don't call getFriends before addFriend
		user = new User();
		user.addFriend(new User());
		user.addFriend(new User());
		assertEquals(2, user.getFriends().size());
	}

	/**
	 * tests that the user's name is always set to lowercase
	 * 
	 * @see DocumentTest#userName()
	 */
	@Test
	public void name() {
		assertEquals(null, new User().getName());
		assertEquals("testuser", new User("TeStUsEr").getName());

		final User user = new User();
		user.setName("TeStUsEr");
		assertEquals("testuser", user.getName());
		user.setName(null);
		assertNull(user.getName());
	}

	/**
	 * tests isSpammer
	 */
	@Test
	public void isSpammer() {
		final User user = new User();
		
		/*
		 * default: unknown spam status
		 */
		assertNull(user.getSpammer());

		// is a spammer
		for (final Boolean spammer : new Boolean[] { true, new Boolean("true") }) {
			user.setSpammer(spammer);
			assertTrue(user.getSpammer());
		}

		// isn't a spammer
		for (final Boolean spammer : new Boolean[] { false, new Boolean("false"), new Boolean("aslkjdfh") }) {
			user.setSpammer(spammer);
			assertFalse(user.getSpammer());
		}

		// isn't a spammer
		// isSpammer maps null to false
		for (final Boolean spammer : new Boolean[] { null, false, new Boolean("false"), new Boolean("aslkjdfh") }) {
			user.setSpammer(spammer);
			assertFalse(user.isSpammer());
		}

	}
	
	/**
	 * Tests remote id setters and lists
	 */
	@Test
	public void testRemoteIds() {
		final User srcUser = ModelUtils.getUser();
		assertEquals("preCondition1", "test-ldapId", srcUser.getLdapId());
		assertEquals("preCondition2", "http://test-openid", srcUser.getOpenID());
//		int openIdRIdFound = 0;
//		int ldapRIdFound = 0;
//		for (RemoteUserId rId : srcUser.getRemoteUserIds()) {
//			if (rId instanceof LdapRemoteUserId) {
//				Assert.assertEquals("test-ldapId", ((LdapRemoteUserId) rId).getRemoteUserId());
//				ldapRIdFound++;
//			} else if (rId instanceof OpenIdRemoteUserId) {
//				Assert.assertEquals("http://test-openid", ((OpenIdRemoteUserId) rId).getRemoteUserId());
//				openIdRIdFound++;
//			}
//		}
//		assertEquals(ldapRIdFound, 1);
//		assertEquals(openIdRIdFound, 1);
		
		srcUser.setLdapId(null);
		
//		ldapRIdFound = 0;
//		openIdRIdFound = 0;
//		for (RemoteUserId rId : srcUser.getRemoteUserIds()) {
//			if (rId instanceof LdapRemoteUserId) {
//				ldapRIdFound++;
//			} else if (rId instanceof OpenIdRemoteUserId) {
//				openIdRIdFound++;
//			}
//		}
//		assertEquals(ldapRIdFound, 0);
//		assertEquals(openIdRIdFound, 1);
		assertNull(srcUser.getLdapId());
		
		srcUser.setOpenID("http://huhu");
		
//		ldapRIdFound = 0;
//		openIdRIdFound = 0;
//		for (RemoteUserId rId : srcUser.getRemoteUserIds()) {
//			if (rId instanceof LdapRemoteUserId) {
//				ldapRIdFound++;
//			} else if (rId instanceof OpenIdRemoteUserId) {
//				AassertEquals("http://huhu", ((OpenIdRemoteUserId) rId).getRemoteUserId());
//				openIdRIdFound++;
//			}
//		}
//		
//		Assert.assertEquals(ldapRIdFound, 0);
//		Assert.assertEquals(openIdRIdFound, 1);
		
		assertEquals("http://huhu", srcUser.getOpenID());
	}
	
	/**
	 * FIXME: merge with testRemoteIds()
	 */
	@Test
	@Ignore
	public void testSetterInfluenceOnRemoteUserIds() {
		final User user = new User();
		final String openID = "openID_id";
		final String ldapId = "ldap_id";
		user.setOpenID(openID);
		user.setLdapId(ldapId);
		assertEquals(2, user.getRemoteUserIds().size());
		user.setLdapId(null);
		assertEquals(1, user.getRemoteUserIds().size());
		//user.setRemoteUserId(new LdapRemoteUserId(ldapId));
		assertEquals(2, user.getRemoteUserIds().size());
		/*
		 * This test currently fails since setting remoteUserIds has no impact on the 
		 * simple attribute ldapId. (It works however vice versa).
		 */
		assertEquals(ldapId, user.getLdapId());
	}
	
	
	@Test
	public void testRemoteUserIdProviderOverwrites() {
		final User user = new User();
		
		final String indentityProviderId = "provider1";
		final String userId = "u_id1_1";
		user.setRemoteUserId(new SamlRemoteUserId(indentityProviderId, userId));
		final String indentityProviderId2 = "provider2";
		final String userId2 = "u_id2";
		user.setRemoteUserId(new SamlRemoteUserId(indentityProviderId2, userId2));
		final String userId3 = "u_id1_2";
		user.setRemoteUserId(new SamlRemoteUserId(indentityProviderId, userId3));
		assertEquals(2 /* 4 */, user.getRemoteUserIds().size());
		assertTrue(user.getRemoteUserIds().contains(new SamlRemoteUserId(indentityProviderId, userId3)));
	}
}