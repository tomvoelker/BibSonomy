package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.util.collection.BiMap;

/**
 * abstract class for enums
 * 
 * @author dzo
 * @param <T>
 */
public abstract class AbstractEnumTypeHandlerCallback<T> extends AbstractTypeHandlerCallback {

	protected abstract T getDefaultValue();

	protected abstract BiMap<T, Integer> getMapping();

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(this.getMapping().get(this.getDefaultValue()));
		} else {
			final T entity = (T) parameter;
			setter.setInt(this.getMapping().get(entity).intValue());
		}
	}

	@Override
	public Object valueOf(final String s) {
		try {
			return this.getMapping().getKeyByValue(Integer.parseInt(s));
		} catch (final NumberFormatException ex) {
			return this.getDefaultValue();
		}
	}
}
