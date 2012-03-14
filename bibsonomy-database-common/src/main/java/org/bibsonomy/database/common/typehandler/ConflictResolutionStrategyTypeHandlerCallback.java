package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;

import org.bibsonomy.model.sync.ConflictResolutionStrategy;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.engine.type.TypeHandler;

/**
 * {@link TypeHandler} for {@link ConflictResolutionStrategy}
 * 
 * @author wla
 * @version $Id$
 */
public class ConflictResolutionStrategyTypeHandlerCallback extends AbstractTypeHandlerCallback {
	
	@Override
	public void setParameter(final ParameterSetter setter, final Object parameter) throws SQLException {
		if (parameter == null) {
			throw new IllegalArgumentException("given conflict resolution strategy is null");
		} else {
			if (parameter instanceof ConflictResolutionStrategy) {
				setter.setString(((ConflictResolutionStrategy)parameter).getConflictResolutionStrategy());
			} else {
				throw new IllegalArgumentException("given object isn't a instance of ConflictResolutionStartegy");
			}
		}
	}

	@Override
	public Object valueOf(final String str) {
		return ConflictResolutionStrategy.getConflictResolutionStrategyByString(str);
	}

}
