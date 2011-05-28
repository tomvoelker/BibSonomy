package org.bibsonomy.webapp.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.GroupingCommand;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.junit.Test;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;


/**
 * @author dzo
 * @version $Id$
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
		command.setAbstractGrouping(GroupUtils.getPublicGroup().getName());
		command.setGroups(Collections.singletonList("test"));
		
		errors = this.validate(command);
		assertEquals(1, errors.getFieldErrorCount("groups"));
		
		/*
		 * test same with private
		 */
		command.setAbstractGrouping(GroupUtils.getPrivateGroup().getName());
		
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
	
	private static final class MockGroupingCommand implements GroupingCommand {
		private String abstractGrouping;
		private List<String> groups;
		
		/**
		 * @return the abstractGrouping
		 */
		@Override
		public String getAbstractGrouping() {
			return this.abstractGrouping;
		}
		
		/**
		 * @param abstractGrouping the abstractGrouping to set
		 */
		@Override
		public void setAbstractGrouping(String abstractGrouping) {
			this.abstractGrouping = abstractGrouping;
		}
		
		/**
		 * @return the groups
		 */
		@Override
		public List<String> getGroups() {
			return this.groups;
		}
		
		/**
		 * @param groups the groups to set
		 */
		@Override
		public void setGroups(List<String> groups) {
			this.groups = groups;
		}
	}
}
