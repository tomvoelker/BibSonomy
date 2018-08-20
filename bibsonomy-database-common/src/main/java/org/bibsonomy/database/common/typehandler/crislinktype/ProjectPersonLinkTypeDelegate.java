package org.bibsonomy.database.common.typehandler.crislinktype;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.model.cris.CRISLinkType;
import org.bibsonomy.model.cris.ProjectPersonLinkType;
import org.bibsonomy.util.collection.BiHashMap;
import org.bibsonomy.util.collection.BiMap;

import java.sql.SQLException;

/**
 * delegate for a {@link ProjectPersonLinkType}
 *
 * @author dzo
 */
public class ProjectPersonLinkTypeDelegate implements CRISLinkTypeTypeHandlerCallbackDelegate {

	protected static final BiMap<ProjectPersonLinkType, Integer> LOOKUP_MAP = new BiHashMap<>();

	static {
		LOOKUP_MAP.put(ProjectPersonLinkType.MANAGER, Integer.valueOf(1));
		LOOKUP_MAP.put(ProjectPersonLinkType.MEMBER, Integer.valueOf(2));
	}

	@Override
	public boolean canHandle(final Class<?> typeClass) {
		return ProjectPersonLinkType.class.equals(typeClass);
	}

	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(LOOKUP_MAP.get(ProjectPersonLinkType.MEMBER));
		} else {
			final ProjectPersonLinkType role = (ProjectPersonLinkType) parameter;
			setter.setInt(LOOKUP_MAP.get(role).intValue());
		}
	}

	@Override
	public CRISLinkType getParameter(final int value) {
		try {
			return LOOKUP_MAP.getKeyByValue(value);
		} catch (final NumberFormatException ex) {
			return ProjectPersonLinkType.MEMBER;
		}
	}
}
