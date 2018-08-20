package org.bibsonomy.database.common.typehandler;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.model.cris.CRISLinkDataSource;
import org.bibsonomy.util.collection.BiHashMap;
import org.bibsonomy.util.collection.BiMap;

import java.sql.SQLException;

/**
 * {@link com.ibatis.sqlmap.client.extensions.TypeHandlerCallback} for {@link org.bibsonomy.model.cris.CRISLinkDataSource}
 *
 * @author dzo
 */
public class CRISLinkDataSourceTypeHandlerCallback extends AbstractTypeHandlerCallback {

	private static final BiMap<CRISLinkDataSource, Integer> CRIS_LINK_DATA_SOURCE_MAPPING = new BiHashMap<>();

	static{
		CRIS_LINK_DATA_SOURCE_MAPPING.put(CRISLinkDataSource.SYSTEM, 0);
		CRIS_LINK_DATA_SOURCE_MAPPING.put(CRISLinkDataSource.USER, 1);
	}

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(CRIS_LINK_DATA_SOURCE_MAPPING.get(CRISLinkDataSource.USER));
		} else {
			final CRISLinkDataSource role = (CRISLinkDataSource) parameter;
			setter.setInt(CRIS_LINK_DATA_SOURCE_MAPPING.get(role).intValue());
		}
	}

	@Override
	public Object valueOf(final String s) {
		try {
			return CRIS_LINK_DATA_SOURCE_MAPPING.getKeyByValue(Integer.parseInt(s));
		} catch (final NumberFormatException ex) {
			return CRISLinkDataSource.USER;
		}
	}
}
