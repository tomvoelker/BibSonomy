package org.bibsonomy.database.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.impl.SqlMapSessionImpl;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMapping;


/**
 * @author dzo
 * @version $Id$
 */
public class DatabaseSchemaInformation {
	private static final Log log = LogFactory.getLog(DatabaseSchemaInformation.class);
	
	private static final String BIBTEX_COMMON_ID = "BibTexCommon.bibtex_common";
	private static final String USER_COMMON_ID = "UserCommon.user";
	private static final String BIBTEX_DATABASE_NAME = "bibtex";
	private static final String USER_DATABASE_NAME = "user";
	private static final String COLUMN_SIZE = "COLUMN_SIZE";
	
	
	private static final Map<Class<?>, Map<String, Integer>> fieldLength = new HashMap<Class<?>, Map<String,Integer>>();
	
	static {
		/*
		 * get the max field lengths
		 */
		getMaxFieldLengths(BIBTEX_COMMON_ID, BIBTEX_DATABASE_NAME);
		getMaxFieldLengths(USER_COMMON_ID, USER_DATABASE_NAME);
	}

	private static void getMaxFieldLengths(final String mappingId, final String tableName) {
		final Map<String, Integer> maxLength = new HashMap<String, Integer>();
		final SqlMapSession sqlMap = DatabaseUtils.getSqlMap();
		
		if (sqlMap instanceof SqlMapSessionImpl) {
			final SqlMapSessionImpl impl = (SqlMapSessionImpl) sqlMap;
			final SqlMapExecutorDelegate delegate = impl.getDelegate();
			final ResultMap resultMap = delegate.getResultMap(mappingId);
			final ResultMapping[] resultMappings = resultMap.getResultMappings();
			
			final Class<?> resultClass = resultMap.getResultClass();
			
			for (final ResultMapping mapping : resultMappings) { 
				final String propertyName = mapping.getPropertyName();
				final String columnName = mapping.getColumnName();
				
				final Integer columnMax = DatabaseSchemaInformation.getSchemaInformation(Integer.class, tableName, columnName, COLUMN_SIZE);
				maxLength.put(propertyName, columnMax);
			}
			
			fieldLength.put(resultClass, maxLength);
		} else {
			log.warn("DatabaseUtils.getSqlMap() isn't an instance of SqlMapSessionImpl. Can't get IBatis Mapping.");
		}
		
		sqlMap.close();
	}
	
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
	private static <R> R getSchemaInformation(final Class<R> resultClass, final String tableNamePattern, final String columnNamePattern, final String columnLabel) {
		final SqlMapSession sqlMap = DatabaseUtils.getSqlMap();
		final DataSource dataSource = sqlMap.getDataSource();
		
		try {
			final DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
			final ResultSet columns = metaData.getColumns(null, null, tableNamePattern, columnNamePattern);
		    while (columns.next()) {
		    	return (R) columns.getObject(columnLabel);
		    }
		} catch (SQLException ex) {
			log.warn("can't get schema informations for column '" + columnNamePattern + "' of table '" + tableNamePattern + "'", ex);
		}	    
	    
	    return null;
	}

	/**
	 * @param resourceClass
	 * @param property
	 * @return the max length of the property of the resource class
	 */
	public static int getMaxColumnLengthForProperty(final Class<?> resourceClass, final String property) {
		final Map<String, Integer> properties = fieldLength.get(resourceClass);
		
		if (present(properties)) {
			final Integer maxLength = properties.get(property);
			return present(maxLength) ? maxLength : -1;
		}
		
		return -1;		
	}
}
