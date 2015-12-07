/**
 * BibSonomy-Database-Common - Helper classes for database interaction
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.common.typehandler;

import java.sql.SQLException;

import org.bibsonomy.model.sync.ConflictResolutionStrategy;

import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.engine.type.TypeHandler;

/**
 * {@link TypeHandler} for {@link ConflictResolutionStrategy}
 * 
 * @author wla
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
