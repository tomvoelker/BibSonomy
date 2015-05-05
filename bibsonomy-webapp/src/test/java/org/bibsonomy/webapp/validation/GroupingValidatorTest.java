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
package org.bibsonomy.webapp.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.GroupingCommand;
import org.bibsonomy.webapp.command.mock.MockGroupingCommand;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;


/**
 * @author dzo
 */
public class GroupingValidatorTest {
	private static final GroupingValidator GROUPING_VALIDATOR = new GroupingValidator();
	
	@Test
	public void testPrivatePublicGrouping() {
		final GroupingCommand command = new MockGroupingCommand();
		
		/*
		 * neither abstractGrouping nor groups present => should fail
		 */
		Errors errors = this.validate(command);
		assertEquals(1, errors.getFieldErrorCount("groups"));
		
		/*
		 * set abstract grouping and set a group (= not valid)
		 */
		command.setAbstractGrouping(GroupUtils.buildPublicGroup().getName());
		command.setGroups(Collections.singletonList("test"));
		
		errors = this.validate(command);
		assertEquals(1, errors.getFieldErrorCount("groups"));
		
		/*
		 * test same with private
		 */
		command.setAbstractGrouping(GroupUtils.buildPrivateGroup().getName());
		
		errors = this.validate(command);
		assertEquals(1, errors.getFieldErrorCount("groups"));
		
		command.setGroups(Collections.<String>emptyList());
		
		errors = this.validate(command);
		assertFalse(errors.hasErrors());
	}
	
	@Test
	public void testOtherGrouping() {
		final GroupingCommand command = new MockGroupingCommand();
		command.setAbstractGrouping(GroupingCommandUtils.OTHER_ABSTRACT_GROUPING);
		/*
		 * no groups present
		 */
		Errors errors = this.validate(command);
		assertEquals(1, errors.getFieldErrorCount("groups"));
		
		/*
		 * add one group
		 */
		command.setGroups(Collections.singletonList("testgroup1"));
		errors = this.validate(command);
		assertFalse(errors.hasErrors());
		
		/* 
		 * add another group
		 * TODO: allow multiple groups => adapt this part of the test
		 */
		command.setGroups(Arrays.asList("testgroup1", "testgroup2"));
		errors = this.validate(command);
		assertEquals(1, errors.getFieldErrorCount("groups"));
		
	}
	
	private Errors validate(GroupingCommand command) {
		@SuppressWarnings("rawtypes")
		final Errors errors = new MapBindingResult(new HashMap(), "command");
		GROUPING_VALIDATOR.validate(command, errors);
		return errors;
	}
}
