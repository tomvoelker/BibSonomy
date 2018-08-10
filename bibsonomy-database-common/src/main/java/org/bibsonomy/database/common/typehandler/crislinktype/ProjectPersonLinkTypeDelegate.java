package org.bibsonomy.database.common.typehandler.crislinktype;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import org.bibsonomy.model.cris.CRISLinkType;
import org.bibsonomy.model.cris.ProjectPersonLinkType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * delegate for a {@link ProjectPersonLinkType}
 *
 * @author dzo
 */
public class ProjectPersonLinkTypeDelegate implements CRISLinkTypeTypeHandlerCallbackDelegate {

	protected static final Map<ProjectPersonLinkType, Integer> LOOKUP_MAP = new HashMap<>();
	protected static final Map<Integer, ProjectPersonLinkType> ID_TYPE_LOOKUP = new HashMap<>();
	static {
		LOOKUP_MAP.put(ProjectPersonLinkType.MANAGER, Integer.valueOf(1));
		LOOKUP_MAP.put(ProjectPersonLinkType.MEMBER, Integer.valueOf(2));

		for (Map.Entry<ProjectPersonLinkType, Integer> entry : LOOKUP_MAP.entrySet()) {
			ID_TYPE_LOOKUP.put(entry.getValue(), entry.getKey());
		}
	}

	@Override
	public boolean canHandle(Class<?> typeClass) {
		return ProjectPersonLinkType.class.equals(typeClass);
	}

	@Override
	public void setParameter(ParameterSetter setter, Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setInt(LOOKUP_MAP.get(ProjectPersonLinkType.MEMBER));
		} else {
			final ProjectPersonLinkType role = (ProjectPersonLinkType) parameter;
			setter.setInt(LOOKUP_MAP.get(role).intValue());
		}
	}

	@Override
	public CRISLinkType getParameter(int value) {
		try {
			return ID_TYPE_LOOKUP.get(value);
		} catch (NumberFormatException ex) {
			return ProjectPersonLinkType.MEMBER;
		}
	}
}
