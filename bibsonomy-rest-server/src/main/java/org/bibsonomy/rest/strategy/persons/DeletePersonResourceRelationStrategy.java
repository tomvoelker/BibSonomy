package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.rest.strategy.AbstractDeleteStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * stragegy for deleting a person resource relation
 *
 * @author dzo
 */
public class DeletePersonResourceRelationStrategy extends AbstractDeleteStrategy {
	private final String personId;
	private final String interHash;
	private final int index;
	private final PersonResourceRelationType type;

	/**
	 * inits a delete strategy for a {@link org.bibsonomy.model.ResourcePersonRelation}
	 * @param context
	 * @param personId
	 * @param interHash
	 * @param index
	 * @param type
	 */
	public DeletePersonResourceRelationStrategy(final Context context, final String personId, final String interHash, final int index, final PersonResourceRelationType type) {
		super(context);
		this.personId = personId;
		this.interHash = interHash;
		this.index = index;
		this.type = type;
	}

	@Override
	protected boolean delete() {
		this.getLogic().removeResourceRelation(this.personId, this.interHash, this.index, this.type);
		return true;
	}
}
