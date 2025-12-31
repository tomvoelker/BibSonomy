package org.bibsonomy.api.config

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import javax.sql.DataSource

/**
 * Logs the resolved datasource configuration on startup to verify that
 * connection URLs are loaded as expected from the local profile.
 *
 * Uses ApplicationReadyEvent instead of a dummy bean to avoid polluting
 * the bean graph with side-effect-only beans.
 */
@Configuration
class DataSourceLoggingConfig(
    @Qualifier("mainDataSource") private val dataSource: DataSource
) {
    private val log = LoggerFactory.getLogger(DataSourceLoggingConfig::class.java)

    /**
     * Log the main datasource once the application context is fully ready.
     * Username is intentionally omitted from logs for security (production logs
     * may be aggregated or accessed by broader teams).
     */
    @EventListener(ApplicationReadyEvent::class)
    fun logMainDataSource() {
        when (dataSource) {
            is BasicDataSource -> log.info(
                "Main datasource resolved: url='{}', driver='{}'",
                dataSource.url,
                dataSource.driverClassName
            )
            else -> log.info("Main datasource resolved: type='{}'", dataSource::class.java.name)
        }
    }
}
