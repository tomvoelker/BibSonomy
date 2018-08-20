package org.bibsonomy.database.common.typehandler;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.bibsonomy.util.collection.BiHashMap;
import org.bibsonomy.util.collection.BiMap;

import java.sql.SQLException;

/**
 * stores the class in the database for the {@link CRISLinkTypeTypeHandlerCallback}
 *
 * @author dzo
 */
public class CRISLinkTypeClassTypeHandlerCallback extends AbstractTypeHandlerCallback {

	protected static final BiMap<Class<?>, Integer> LINK_TYPE_CLASS_ID_MAP = new BiHashMap<>();

	static {
		LINK_TYPE_CLASS_ID_MAP.put(ProjectPersonLinkType.class, Integer.valueOf(1));
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
