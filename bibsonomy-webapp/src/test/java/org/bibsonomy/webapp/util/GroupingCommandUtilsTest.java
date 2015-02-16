/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.GroupingCommand;
import org.bibsonomy.webapp.command.mock.MockGroupingCommand;
import org.junit.Test;

/**
 * @author dzo
 */
public class GroupingCommandUtilsTest {

	private static final String GROUP_NAME_1 = "testgroup1";
	private static final String GROUP_NAME_2 = "testgroup2";
	
	private static final Group PUBLIC_GROUP = GroupUtils.getPublicGroup();
	private static final Group PRIVATE_GROUP = GroupUtils.buildPrivateGroup();
	
	/**
	 * test {@link GroupingCommandUtils#initCommandGroups(GroupingCommand, Set)}
	 */
	@Test
	public void testInitCommandGroups() {
		final List<String> commandGroups = new LinkedList<String>();
		final GroupingCommand command = new MockGroupingCommand();
		command.setGroups(commandGroups);
		GroupingCommandUtils.initCommandGroups(command, new HashSet<Group>(Arrays.asList(new Group(GROUP_NAME_1), new Group(GROUP_NAME_2))));
		
		assertEquals(GroupingCommandUtils.OTHER_ABSTRACT_GROUPING, command.getAbstractGrouping());
		
		assertEquals(2, commandGroups.size());
		assertTrue(commandGroups.contains(GROUP_NAME_1));
		assertTrue(commandGroups.contains(GROUP_NAME_2));
		
		// public test
		GroupingCommandUtils.initCommandGroups(command, Collections.singleton(PUBLIC_GROUP));
		assertEquals(PUBLIC_GROUP.getName(), PUBLIC_GROUP.getName());
		assertEquals(0, commandGroups.size());
		
		// private test
		GroupingCommandUtils.initCommandGroups(command, Collections.singleton(PRIVATE_GROUP));
		assertEquals(PRIVATE_GROUP.getName(), PRIVATE_GROUP.getName());
		assertEquals(0, commandGroups.size());
	}
	
	/**
	 * tests {@link GroupingCommandUtils#initGroups(GroupingCommand, Set)}
	 */
	@Test
	public void testInitGroups() {
		final List<String> commandGroups = new LinkedList<String>();
		final GroupingCommand command = new MockGroupingCommand();
		command.setGroups(commandGroups);
		
		final Set<Group> groupsToInit = new HashSet<Group>();
		
		commandGroups.add(GROUP_NAME_1); // ignores this group
		command.setAbstractGrouping(PUBLIC_GROUP.getName());
		GroupingCommandUtils.initGroups(command, groupsToInit);
		
		assertEquals(1, groupsToInit.size());
		assertTrue(groupsToInit.contains(PUBLIC_GROUP));
		
		command.setAbstractGrouping(PRIVATE_GROUP.getName());
		
		GroupingCommandUtils.initGroups(command, groupsToInit);
		
		assertEquals(1, groupsToInit.size());
		assertTrue(groupsToInit.contains(PRIVATE_GROUP));
		
		groupsToInit.clear(); // clear it
		command.setAbstractGrouping(GroupingCommandUtils.OTHER_ABSTRACT_GROUPING);
		GroupingCommandUtils.initGroups(command, groupsToInit);
		
		assertEquals(1, groupsToInit.size());
		assertTrue(groupsToInit.contains(new Group(GROUP_NAME_1)));
	}

}
