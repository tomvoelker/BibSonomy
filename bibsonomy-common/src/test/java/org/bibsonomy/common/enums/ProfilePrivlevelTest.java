/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.common.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @author dzo
 * @version $Id$
 */
public class ProfilePrivlevelTest {
	private static final String FRIENDS_STR = "friends";
	private static final String PRIVATE_STR = "private";
	private static final String PUBLIC_STR = "public";
	private static final String INVALID_STR = "42";

	/**
	 * tests {@link ProfilePrivlevel#getProfilePrivlevel(int)}
	 */
	@Test
	public void getProfilePrivlevel() {
		// test all profile priv level values
		for (final ProfilePrivlevel profilePrivlevel : ProfilePrivlevel.values()) {	
			assertEquals(profilePrivlevel, ProfilePrivlevel.getProfilePrivlevel(profilePrivlevel.getProfilePrivlevel()));
		}
	}
	
	/**
	 * tests {@link ProfilePrivlevel#getProfilePrivlevel(int)} invalid ids
	 */
	@Test
	public void getInvalidPrivlevel() {
		for (final int profilePrivlevel : new int[] { -1, 42 }) {
			try {
				ProfilePrivlevel.getProfilePrivlevel(profilePrivlevel);
				fail("RuntimeException expected");
			} catch (final RuntimeException ex) {
			}
		}		
	}
	
	/**
	 * tests {@link ProfilePrivlevel#getProfilePrivlevel(String)}
	 */
	@Test
	public void getProfilePrivlevelByString() {
		assertEquals(ProfilePrivlevel.PUBLIC, ProfilePrivlevel.getProfilePrivlevel(PUBLIC_STR));
		assertEquals(ProfilePrivlevel.PRIVATE, ProfilePrivlevel.getProfilePrivlevel(PRIVATE_STR));
		assertEquals(ProfilePrivlevel.FRIENDS, ProfilePrivlevel.getProfilePrivlevel(FRIENDS_STR));

		assertEquals(ProfilePrivlevel.PRIVATE, ProfilePrivlevel.getProfilePrivlevel(INVALID_STR));
	}
	
	/**
	 * tests {@link ProfilePrivlevel#isProfilePrivlevel(String)}
	 */
	@Test
	public void isProfilePrivLevel() {
		assertTrue(ProfilePrivlevel.isProfilePrivlevel(PUBLIC_STR));
		assertTrue(ProfilePrivlevel.isProfilePrivlevel(PRIVATE_STR));
		assertTrue(ProfilePrivlevel.isProfilePrivlevel(FRIENDS_STR));
		
		assertFalse(ProfilePrivlevel.isProfilePrivlevel(INVALID_STR));
	}
}
