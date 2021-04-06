package org.bibsonomy.database.common.typehandler;

import org.bibsonomy.database.common.enums.LogReason;
import org.bibsonomy.util.collection.BiHashMap;
import org.bibsonomy.util.collection.BiMap;

/**
 * type handler for {@link LogReason}
 *
 * @author dzo
 */
public class LogReasonTypeHandlerCallback extends AbstractEnumTypeHandlerCallback<LogReason> {

	private static final BiMap<LogReason, Integer> LOG_REASON_MAPPING = new BiHashMap<>();

	static{
		LOG_REASON_MAPPING.put(LogReason.UPDATED, 0);
		LOG_REASON_MAPPING.put(LogReason.DELETED, 1);
		LOG_REASON_MAPPING.put(LogReason.LINKED_ENTITY_UPDATE, 2);
	}

	@Override
	protected LogReason getDefaultValue() {
		return LogReason.DELETED;
	}

	@Override
	protected BiMap<LogReason, Integer> getMapping() {
		return LOG_REASON_MAPPING;
	}
}
