package org.bibsonomy.model.logic.exception;

import java.util.List;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;

/**
 * TODO: add documentation to this class
 *
 * @author jensi
 */
public class ResourcePersonAlreadyAssignedException extends LogicException {
	private static final long serialVersionUID = 1526222655037790865L;
	
	private final ResourcePersonRelation existingRelation;
	
	public ResourcePersonAlreadyAssignedException(final ResourcePersonRelation existingRelation) {
		this.existingRelation = existingRelation;
	}

	public ResourcePersonRelation getExistingRelation() {
		return this.existingRelation;
	}

	public PersonName getPubPersonName() {
		List<PersonName> names = this.existingRelation.getPost().getResource().getPersonNamesByRole(this.existingRelation.getRelationType());
		if (names == null) {
			return null;
		}
		return names.get(this.existingRelation.getPersonIndex());
	}
}
