package org.bibsonomy.api.config

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * Logs the resolved datasource configuration on startup to verify that
 * credentials and URLs are loaded as expected from the local profile.
 */
@Configuration
class DataSourceLoggingConfig {
    private val log = LoggerFactory.getLogger(DataSourceLoggingConfig::class.java)

    /**
     * Log the main datasource as soon as it is instantiated. This bean has no
     * side effects besides the log statement and returns a dummy value.
     */
    @Bean
    fun logMainDataSource(@Qualifier("mainDataSource") dataSource: DataSource): Any {
        when (dataSource) {
            is BasicDataSource -> log.info(
                "Main datasource resolved: url='{}', user='{}', driver='{}'",
                dataSource.url,
                dataSource.username,
                dataSource.driverClassName
            )
            else -> log.info("Main datasource resolved: type='{}'", dataSource::class.java.name)
        }
        return Any()
    }
}
