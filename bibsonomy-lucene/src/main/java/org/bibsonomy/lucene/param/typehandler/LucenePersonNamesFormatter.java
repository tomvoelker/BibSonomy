package org.bibsonomy.lucene.param.typehandler;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;


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

	/** 
	 * FIXME: improve error handling;
	 * 
	 * @see org.bibsonomy.lucene.param.typehandler.LuceneTypeHandler#setValue(java.lang.String)
	 */
	@Override
	public List<PersonName> setValue(String str) {
		try {
			return PersonNameUtils.discoverPersonNames(str);
		} catch (PersonListParserException e) {
			return Collections.emptyList();
		}
	}
}
