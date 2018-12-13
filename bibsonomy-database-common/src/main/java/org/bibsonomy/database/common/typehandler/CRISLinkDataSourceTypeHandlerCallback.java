package org.bibsonomy.database.common.typehandler;

import org.bibsonomy.model.cris.CRISLinkDataSource;
import org.bibsonomy.util.collection.BiHashMap;
import org.bibsonomy.util.collection.BiMap;

/**
 * {@link com.ibatis.sqlmap.client.extensions.TypeHandlerCallback} for {@link org.bibsonomy.model.cris.CRISLinkDataSource}
 *
 * @author dzo
 */
public class CRISLinkDataSourceTypeHandlerCallback extends AbstractEnumTypeHandlerCallback<CRISLinkDataSource> {

	private static final BiMap<CRISLinkDataSource, Integer> CRIS_LINK_DATA_SOURCE_MAPPING = new BiHashMap<>();

	static{
		CRIS_LINK_DATA_SOURCE_MAPPING.put(CRISLinkDataSource.SYSTEM, 0);
		CRIS_LINK_DATA_SOURCE_MAPPING.put(CRISLinkDataSource.USER, 1);
	}

	@Override
	protected CRISLinkDataSource getDefaultValue() {
		return CRISLinkDataSource.USER;
	}

	@Override
	protected BiMap<CRISLinkDataSource, Integer> getMapping() {
		return CRIS_LINK_DATA_SOURCE_MAPPING;
	}
}
