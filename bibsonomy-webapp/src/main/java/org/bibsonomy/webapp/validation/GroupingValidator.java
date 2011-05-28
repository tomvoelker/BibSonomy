package org.bibsonomy.webapp.validation;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.webapp.command.GroupingCommand;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author dzo
 * @version $Id$
 */
public class GroupingValidator implements Validator<GroupingCommand> {
	private static final Log log = LogFactory.getLog(GroupingValidator.class);

	
	private static final Group PUBLIC_GROUP = GroupUtils.getPublicGroup();
	private static final Group PRIVATE_GROUP = GroupUtils.getPrivateGroup();
	
	@Override
	public boolean supports(final Class<?> clazz) {
		return clazz != null && GroupingCommand.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors) {
		Assert.notNull(target);
		final GroupingCommand command = (GroupingCommand) target;
		final String abstractGrouping = command.getAbstractGrouping();
		final List<String> groups = command.getGroups();
		
		if (PUBLIC_GROUP.getName().equals(abstractGrouping) || PRIVATE_GROUP.getName().equals(abstractGrouping)) {
			if (present(groups)) {
				/*
				 * "public" or "private" selected, but other group(s) chosen
				 */
				errors.rejectValue("groups", "error.field.valid.groups");
			}
		} else if (GroupingCommandUtils.OTHER_ABSTRACT_GROUPING.equals(abstractGrouping)) {
			log.debug("grouping 'other' found ... checking given groups");
			if (groups == null || groups.isEmpty()) {
				log.debug("error: no groups given");
				/*
				 * "other" selected, but no group chosen
				 * TODO: more detailed error messages for different errors
				 */
				errors.rejectValue("groups", "error.field.valid.groups");
			} else if (groups.size() > 1) {
				/*
				 * TODO: allow multiple groups
				 */
				errors.rejectValue("groups", "error.field.valid.groups");
			}
		} else {
			log.debug("neither public, private, other chosen");
			/*
			 * neither public, private, other chosen
			 */
			errors.rejectValue("groups", "error.field.valid.groups");
		}
	}

}
