package org.bibsonomy.database.common.typehandler;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.model.cris.ProjectPersonLinkType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * stores the class in the database for the {@link CRISLinkTypeTypeHandlerCallback}
 *
 * @author dzo
 */
public class CRISLinkTypeClassTypeHandlerCallback extends AbstractTypeHandlerCallback {

	protected static final Map<Class<?>, Integer> LINK_TYPE_CLASS_ID_MAP = new HashMap<>();
	protected static final Map<Integer, Class<?>> ID_LINK_TYPE_CLASS_MAP = new HashMap<>();

	static {
		LINK_TYPE_CLASS_ID_MAP.put(ProjectPersonLinkType.class, Integer.valueOf(1));

		for (Map.Entry<Class<?>, Integer> entry : LINK_TYPE_CLASS_ID_MAP.entrySet()) {
			ID_LINK_TYPE_CLASS_MAP.put(entry.getValue(), entry.getKey());
		}
	}

	@Override
	public void setParameter(ParameterSetter parameterSetter, Object o) throws SQLException {
		final Class<?> parameterClass = o.getClass();
		parameterSetter.setInt(LINK_TYPE_CLASS_ID_MAP.get(parameterClass).intValue());
	}

	@Override
	public Object valueOf(String s) {
		throw new UnsupportedOperationException();
	}
}
