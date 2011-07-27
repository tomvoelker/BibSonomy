package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;

import org.bibsonomy.model.sync.SynchronizationDirection;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;

public class SyncDirectionTypeHandlerCallback extends AbstractTypeHandlerCallback {
	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			setter.setString(SynchronizationDirection.BOTH.getSynchronizationDirection());
		} else {
			setter.setString(((SynchronizationDirection) parameter).getSynchronizationDirection());
		}
	}

	@Override
	public Object valueOf(final String str) {
		return SynchronizationDirection.getSynchronizationDirectionByString(str);
	}
}
