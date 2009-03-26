package beans;

import java.util.Set;

import org.springframework.beans.propertyeditors.CustomCollectionEditor;

/**
 * This class is an ugly hack to allow setting of collection properties
 * of beans in the old code.
 *
 * In particular, it's used in processBibtex.jsp to set the 
 * {@link BibtexHandlerBean#setRelevantFor(Set)} property from the relevantFor
 * input form. Since this is a set, this seems to be not supported by the 
 * standard JSP/JSTL mechanisms (with Spring it's no problem).
 * 
 * FIXME: Remove this code, when edit_bibtex.jsp is transferred!
 * 
 * @author rja
 * @version $Id$
 */
public class SetPropertyEditor extends CustomCollectionEditor {

	public SetPropertyEditor() {
		super(Set.class);
	}
	
	public SetPropertyEditor(Class collectionType) {
		super(collectionType);
	}

}
