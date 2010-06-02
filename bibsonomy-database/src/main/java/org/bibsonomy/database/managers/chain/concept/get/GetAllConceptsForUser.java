package org.bibsonomy.database.managers.chain.concept.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.concept.ConceptChainElement;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.model.Tag;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetAllConceptsForUser extends ConceptChainElement {

	@Override
	protected List<Tag> handle(final TagRelationParam param, final DBSession session) {
		return this.db.getAllConceptsForUser(param, session);
	}

	@Override
	protected boolean canHandle(final TagRelationParam param) {
		return (param.getGrouping() == GroupingEntity.USER &&
				param.getConceptStatus() == ConceptStatus.ALL &&
		   		present(param.getRequestedUserName()));
	}
}