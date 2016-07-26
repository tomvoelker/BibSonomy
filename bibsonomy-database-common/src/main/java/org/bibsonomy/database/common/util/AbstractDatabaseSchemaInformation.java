/**
 * BibSonomy-Database-Common - Helper classes for database interaction
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.database.common.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.services.database.DatabaseSchemaInformation;

import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.impl.SqlMapSessionImpl;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMapping;
import com.ibatis.sqlmap.engine.type.CustomTypeHandler;
import com.ibatis.sqlmap.engine.type.TypeHandler;

/**
 * 
 * @author dzo
 */
public class AbstractDatabaseSchemaInformation implements DatabaseSchemaInformation {
	private static final Log log = LogFactory.getLog(AbstractDatabaseSchemaInformation.class);

	protected static final String COLUMN_SIZE = "COLUMN_SIZE";

	private final Map<Class<?>, Map<String, Integer>> fieldLength = new HashMap<Class<?>, Map<String, Integer>>();
	private final Map<Class<?>, Map<String, CustomTypeHandler>> typeHandlers = new HashMap<Class<?>, Map<String, CustomTypeHandler>>();

	/**
	 * returns meta informations of the database
	 * 
	 * @param <R>
	 * @param resultClass
	 * @param tableNamePattern
	 * @param columnNamePattern
	 * @param columnLabel
	 * @param sqlMapSession
	 * 
	 * @return the schema information of the column of the table
	 */
	@SuppressWarnings("unchecked")
	protected static <R> R getSchemaInformation(/* only used for the cast */final Class<R> resultClass, final String tableNamePattern, final String columnNamePattern, final String columnLabel, final SqlMapSession sqlMapSession) {
		final DataSource dataSource = sqlMapSession.getDataSource();

		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			final DatabaseMetaData metaData = connection.getMetaData();
			final ResultSet columns = metaData.getColumns(null, null, tableNamePattern, columnNamePattern);
			while (columns.next()) {
				return (R) columns.getObject(columnLabel);
			}
		} catch (final SQLException ex) {
			log.warn("can't get schema informations for column '" + columnNamePattern + "' of table '" + tableNamePattern + "'", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (final SQLException ex) {
					log.warn("can't close connection", ex);
				}
			}
		}

		return null;
	}

	protected void insertMaxFieldLengthsAndTypeHandlers(final String mappingId, final String tableName, final SqlMapSession sqlMap) {
		final Map<String, Integer> maxLength = new HashMap<String, Integer>();
		final Map<String, CustomTypeHandler> classTypeHandlers = new HashMap<String, CustomTypeHandler>();
		if (sqlMap instanceof SqlMapSessionImpl) {
			final SqlMapSessionImpl impl = (SqlMapSessionImpl) sqlMap;
			final SqlMapExecutorDelegate delegate = impl.getDelegate();
			final ResultMap resultMap = delegate.getResultMap(mappingId);
			final ResultMapping[] resultMappings = resultMap.getResultMappings();
			
			final Class<?> resultClass = resultMap.getResultClass();

			for (final ResultMapping mapping : resultMappings) {
				final String propertyName = mapping.getPropertyName();
				final String columnName = mapping.getColumnName();
				final TypeHandler typeHandler = mapping.getTypeHandler();
				if (typeHandler instanceof CustomTypeHandler) {
					CustomTypeHandler customTypeHandler = (CustomTypeHandler) typeHandler;
					classTypeHandlers.put(propertyName, customTypeHandler);
				}

				final Integer columnMax = getSchemaInformation(Integer.class, tableName, columnName, COLUMN_SIZE, sqlMap);
				maxLength.put(propertyName, columnMax);
			}

			this.fieldLength.put(resultClass, maxLength);
			this.typeHandlers.put(resultClass, classTypeHandlers);
		} else {
			log.warn("SqlMapSession isn't an instance of SqlMapSessionImpl. Can't get iBatis Mapping.");
		}
	}
	
	
	@Override
	public <T> T callTypeHandler(final Class<?> resourceClass, final String property, final Object type, final Class<T> requiredClass) {
		final Map<String, CustomTypeHandler> typehandlersForClass = this.typeHandlers.get(resourceClass);
		if (typehandlersForClass == null) {
			return null;
		}
		
		final CustomTypeHandler typeHandlerForProperty = typehandlersForClass.get(property);
		if (typeHandlerForProperty != null) {
			final DummyPreparedStatement ps = new DummyPreparedStatement();
			try {
				final int index = 0;
				typeHandlerForProperty.setParameter(ps, 0, type, "");
				final Object object = ps.getParameters().get(Integer.valueOf(index));
				if (requiredClass.isInstance(object)) {
					@SuppressWarnings("unchecked") // ok, we check it
					final T returnValue = (T) object;
					return returnValue;
				}
			} catch (SQLException e) {
				log.error("error calling type handler", e);
			} finally {
				try {
					ps.close();
				} catch (SQLException e) {
					log.error("this will never ever happen", e);
				}
			}
		}
		
		return null;
	}

	@Override
	public int getMaxColumnLengthForProperty(final Class<?> resourceClass, final String property) {
		final Map<String, Integer> properties = this.fieldLength.get(resourceClass);

		if (present(properties)) {
			final Integer maxLength = properties.get(property);
			return present(maxLength) ? maxLength.intValue() : -1;
		}

		final Class<?> superclass = resourceClass.getSuperclass();
		if (!Object.class.equals(superclass)) {
			return this.getMaxColumnLengthForProperty(superclass, property);
		}

		return -1;
	}

}
