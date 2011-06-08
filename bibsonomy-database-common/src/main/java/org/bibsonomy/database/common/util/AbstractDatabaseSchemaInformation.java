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

/**
 * 
 * @author dzo
 * @version $Id$
 */
public class AbstractDatabaseSchemaInformation implements DatabaseSchemaInformation {
    private static final Log log = LogFactory.getLog(AbstractDatabaseSchemaInformation.class);

    protected static final String COLUMN_SIZE = "COLUMN_SIZE";
    
    
    private final Map<Class<?>, Map<String, Integer>> fieldLength = new HashMap<Class<?>, Map<String, Integer>>();

    /**
     * returns meta informations of the database
     * 
     * @param <R>
     * @param resultClass
     * @param tableNamePattern
     * @param columnNamePattern
     * @param columnLabel
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
	    final ResultSet columns = metaData.getColumns(null, null,
		    tableNamePattern, columnNamePattern);
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

    protected void insertMaxFieldLengths(final String mappingId, final String tableName, final SqlMapSession sqlMap) {
	final Map<String, Integer> maxLength = new HashMap<String, Integer>();

	if (sqlMap instanceof SqlMapSessionImpl) {
	    final SqlMapSessionImpl impl = (SqlMapSessionImpl) sqlMap;
	    final SqlMapExecutorDelegate delegate = impl.getDelegate();
	    final ResultMap resultMap = delegate.getResultMap(mappingId);
	    final ResultMapping[] resultMappings = resultMap
		    .getResultMappings();

	    final Class<?> resultClass = resultMap.getResultClass();

	    for (final ResultMapping mapping : resultMappings) {
		final String propertyName = mapping.getPropertyName();
		final String columnName = mapping.getColumnName();

		final Integer columnMax = getSchemaInformation(Integer.class, tableName, columnName, COLUMN_SIZE, sqlMap);
		maxLength.put(propertyName, columnMax);
	    }

	    this.fieldLength.put(resultClass, maxLength);
	} else {
	    log.warn("SqlMapSession isn't an instance of SqlMapSessionImpl. Can't get iBatis Mapping.");
	}
    }

    @Override
    public int getMaxColumnLengthForProperty(final Class<?> resourceClass, final String property) {
	final Map<String, Integer> properties = this.fieldLength.get(resourceClass);

	if (present(properties)) {
	    final Integer maxLength = properties.get(property);
	    return present(maxLength) ? maxLength : -1;
	}

	final Class<?> superclass = resourceClass.getSuperclass();
	if (!Object.class.equals(superclass)) {
	    return this.getMaxColumnLengthForProperty(superclass, property);
	}

	return -1;
    }

}
