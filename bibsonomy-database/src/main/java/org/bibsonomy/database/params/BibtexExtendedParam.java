package org.bibsonomy.database.params;

import org.bibsonomy.model.extra.ExtendedField;

/**
 * @author philipp
 * @version $Id$
 */
public class BibtexExtendedParam extends GenericParam {

    private ExtendedField extendedField;

    /**
     * @param extendedField the extendedField to set
     */
    public void setExtendedField(ExtendedField extendedField) {
	this.extendedField = extendedField;
    }

    /**
     * @return the extendedField
     */
    public ExtendedField getExtendedField() {
	return extendedField;
    }
    
    
    
}
