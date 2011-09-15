package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * @version $Id$
 */
public class PersonNameTypeHandlerCallback extends AbstractTypeHandlerCallback {

	@SuppressWarnings("unchecked")
	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setString(null);
		} else {
			setter.setString(PersonNameUtils.serializePersonNames(((List<PersonName>) parameter), true));
		}
	}

	@Override
	public Object valueOf(final String str) {
		try {
			return PersonNameUtils.discoverPersonNames(str);
		} catch (PersonListParserException e) {
			return Collections.emptyList();
		}
	}
}