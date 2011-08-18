package org.bibsonomy.lucene.param.typehandler;

import java.util.List;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameUtils;


/**
 * convert a list of persons to its string representation and back
 * 
 * @author rja
 * @version $Id$
 */
public class LucenePersonNamesFormatter extends AbstractTypeHandler<List<PersonName>> {

	@Override
	public String getValue(List<PersonName> obj) {
		return PersonNameUtils.serializePersonNames(obj);
	}

	@Override
	public List<PersonName> setValue(String str) {
		return PersonNameUtils.discoverPersonNames(str);
	}
}
