package org.bibsonomy.lucene.index.converter;

import org.bibsonomy.es.ESConstants;

/**
 * Names of the constants in this enum are directly used as values of the {@link ESConstants#NORMALIZED_ENTRY_TYPE_FIELD_NAME} field in the index.
 *
 * @author jensi
 */
public enum NormalizedEntryTypes {
	habilitation,
	phdthesis,
	master_thesis,
	bachelor_thesis,
	candidate_thesis 
}
