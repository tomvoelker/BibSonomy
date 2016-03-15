/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 */
public class LucenePersonNamesFormatter extends AbstractTypeHandler<List<PersonName>> {

	@Override
	public String getValue(List<PersonName> obj) {
		final String personString = PersonNameUtils.serializePersonNames(obj);
		if (personString == null) {
			return "";
		}
		
		return personString;
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
