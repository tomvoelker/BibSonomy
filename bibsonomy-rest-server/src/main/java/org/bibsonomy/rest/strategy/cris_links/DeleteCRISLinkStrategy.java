package org.bibsonomy.rest.strategy.cris_links;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.ValidationUtils;

/**
 * TODO: allow deletion of arbitrary cris links
 * @author pda
 */
public class DeleteCRISLinkStrategy extends AbstractDeleteStrategy {
	private final String sourceId;
	private final String targetId;

	/**
	 * @param context
	 */
	public DeleteCRISLinkStrategy(Context context) {
		super(context);
		this.sourceId = context.getStringAttribute("sourceId", null);
		this.targetId = context.getStringAttribute("targetId", null);
	}

	@Override
	protected boolean delete() {
		final Person target = getLogic().getPersonById(PersonIdType.PERSON_ID, targetId);
		if (!ValidationUtils.present(target)) {
			return false;
		}
		final Project source = getLogic().getProjectDetails(sourceId);
		if (!ValidationUtils.present(source)) {
			return false;
		}
		return getLogic().deleteCRISLink(source, target).getStatus() == Status.OK;
	}
}
