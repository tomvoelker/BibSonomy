package org.bibsonomy.database.common.typehandler;

import static org.bibsonomy.util.ValidationUtils.present;

import java.sql.SQLException;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.model.Resource;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

/**
 * 
 * @version $Id$
 */
public class ContentTypeTypeHandlerCallback extends AbstractTypeHandlerCallback {

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (present(parameter) && parameter instanceof Class<?>) {
			setter.setInt(ConstantID.getContentTypeByClass((Class<? extends Resource>)parameter).getId());
		} else {
			throw new UnsupportedResourceTypeException();
		}
	}

	@Override
	public Object valueOf(final String str) {
		try {
			return ConstantID.getClassByContentType(Integer.parseInt(str));
		} catch (final Exception e) {
			throw new UnsupportedResourceTypeException();
		}
	}
}