package org.bibsonomy.database.typehandler;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

/**
 * Implements the {@link TypeHandlerCallback#getResult(ResultGetter)} method of
 * {@link TypeHandlerCallback} by just returning null if getter was null or
 * otherwise the result of the {@link TypeHandlerCallback#valueOf(String)}
 * method using the value provided by {@link ResultGetter#getString()}
 * 
 * @author dzo
 * @version $Id$
 */
public abstract class AbstractTypeHandlerCallback implements TypeHandlerCallback {

	/*
	 * (non-Javadoc)
	 * @see com.ibatis.sqlmap.client.extensions.TypeHandlerCallback#getResult(com.ibatis.sqlmap.client.extensions.ResultGetter)
	 */
	@Override
	public Object getResult(final ResultGetter getter) throws SQLException {
		final String value = getter.getString();
		
		if (getter.wasNull()) {
			return null;
		}
		
		return this.valueOf(value);
	}

}