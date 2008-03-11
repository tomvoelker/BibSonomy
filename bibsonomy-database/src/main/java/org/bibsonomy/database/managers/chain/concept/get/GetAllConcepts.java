package org.bibsonomy.database.managers.chain.concept.get;

import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.concept.ConceptChainElement;
import org.bibsonomy.database.params.TagRelationParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetAllConcepts extends ConceptChainElement {

	@Override
	protected List<Tag> handle(TagRelationParam param, DBSession session) {
		return this.db.getAllConcepts(session);
	}

	@Override
	protected boolean canHandle(TagRelationParam param) {
		return 	param.getGrouping() == GroupingEntity.ALL &&
		param.getConceptStatus().equals(ConceptStatus.ALL);
	}
}