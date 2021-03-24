package org.bibsonomy.database.common.typehandler.crislinktype;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.model.cris.CRISLinkType;
import org.bibsonomy.model.cris.GroupPersonLinkType;
import org.bibsonomy.util.collection.BiHashMap;
import org.bibsonomy.util.collection.BiMap;

import java.sql.SQLException;

/**
 * @author dzo
 */
public class GroupPersonLinktypeDelegate implements CRISLinkTypeTypeHandlerCallbackDelegate {
	private static final BiMap<GroupPersonLinkType, Integer> LOOKUP_MAP = new BiHashMap<>();

	static {
		LOOKUP_MAP.put(GroupPersonLinkType.LEADER, Integer.valueOf(1));
		LOOKUP_MAP.put(GroupPersonLinkType.MEMBER, Integer.valueOf(2));
	}

	@Override
	public boolean canHandle(Class<?> typeClass) {
		return GroupPersonLinkType.class.equals(typeClass);
	}

	@Override
	public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(LOOKUP_MAP.get(GroupPersonLinkType.MEMBER));
		} else {
			final GroupPersonLinkType role = (GroupPersonLinkType) parameter;
			setter.setInt(LOOKUP_MAP.get(role).intValue());
		}
	}

	@Override
	public CRISLinkType getParameter(final int value) {
		try {
			return LOOKUP_MAP.getKeyByValue(value);
		} catch (final NumberFormatException ex) {
			return GroupPersonLinkType.MEMBER;
		}
	}
}
