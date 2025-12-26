package org.springframework.orm.ibatis

import com.ibatis.sqlmap.client.SqlMapClient
import com.ibatis.sqlmap.engine.builder.xml.SqlMapConfigParser
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate
import com.ibatis.sqlmap.engine.transaction.TransactionConfig
import com.ibatis.sqlmap.engine.transaction.TransactionManager
import com.ibatis.sqlmap.engine.transaction.external.ExternalTransactionConfig
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.Resource
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.util.Assert
import org.springframework.util.ObjectUtils
import java.io.IOException
import java.util.Properties
import javax.sql.DataSource

/**
 * Minimal reimplementation of Spring 3's SqlMapClientFactoryBean to keep the
 * legacy iBatis 2 configuration working on Spring 6 without pulling in the old
 * Spring ORM module. Only the feature surface used by the legacy XML
 * (configLocation + dataSource + transactionConfigClass) is supported.
 */
class SqlMapClientFactoryBean : FactoryBean<SqlMapClient>, InitializingBean {

    private var configLocations: Array<Resource>? = null
    private var sqlMapClientProperties: Properties? = null
    private var dataSource: DataSource? = null
    private var useTransactionAwareDataSource: Boolean = true
    private var transactionConfigClass: Class<out TransactionConfig> = ExternalTransactionConfig::class.java
    private var transactionConfigProperties: Properties? = null

    private var sqlMapClient: SqlMapClient? = null

    fun setConfigLocation(configLocation: Resource?) {
        this.configLocations = configLocation?.let { arrayOf(it) }
    }

    fun setConfigLocations(configLocations: Array<Resource>?) {
        this.configLocations = configLocations
    }

    fun setSqlMapClientProperties(sqlMapClientProperties: Properties?) {
        this.sqlMapClientProperties = sqlMapClientProperties
    }

    fun setDataSource(dataSource: DataSource?) {
        this.dataSource = dataSource
    }

    fun setUseTransactionAwareDataSource(useTransactionAwareDataSource: Boolean) {
        this.useTransactionAwareDataSource = useTransactionAwareDataSource
    }

    fun setTransactionConfigClass(transactionConfigClass: Class<out TransactionConfig>?) {
        this.transactionConfigClass = requireNotNull(transactionConfigClass) {
            "transactionConfigClass must not be null"
        }
    }

    fun setTransactionConfigProperties(transactionConfigProperties: Properties?) {
        this.transactionConfigProperties = transactionConfigProperties
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        sqlMapClient = buildSqlMapClient(configLocations, sqlMapClientProperties)
        val ds = dataSource
        if (ds != null) {
            val txConfig = transactionConfigClass.getDeclaredConstructor().newInstance()
            var dsToUse: DataSource = ds
            if (useTransactionAwareDataSource && ds !is TransactionAwareDataSourceProxy) {
                dsToUse = TransactionAwareDataSourceProxy(ds)
            }
            txConfig.dataSource = dsToUse
            val props = transactionConfigProperties ?: defaultTxProps()
            txConfig.initialize(props)
            applyTransactionConfig(sqlMapClient!!, txConfig)
        }
    }

    private fun defaultTxProps(): Properties =
        Properties().apply { setProperty("SetAutoCommitAllowed", "false") }

    @Throws(IOException::class)
    protected fun buildSqlMapClient(
        configLocations: Array<Resource>?,
        properties: Properties?
    ): SqlMapClient {
        Assert.isTrue(!ObjectUtils.isEmpty(configLocations), "At least 1 'configLocation' entry is required")
        var client: SqlMapClient? = null
        val configParser = SqlMapConfigParser()
        configLocations!!.forEach { configLocation ->
            try {
                configLocation.inputStream.use { stream ->
                    client = configParser.parse(stream, properties)
                }
            } catch (ex: RuntimeException) {
                val cause = ex.cause ?: ex
                throw IOException("Failed to parse config resource: $configLocation", cause)
            }
        }
        return client!!
    }

    protected fun applyTransactionConfig(client: SqlMapClient, txConfig: TransactionConfig) {
        if (client !is ExtendedSqlMapClient) {
            throw IllegalArgumentException(
                "Cannot set TransactionConfig with DataSource for SqlMapClient if not of type ExtendedSqlMapClient: $client"
            )
        }
        val delegate: SqlMapExecutorDelegate = client.delegate
        txConfig.maximumConcurrentTransactions = delegate.maxTransactions
        delegate.txManager = TransactionManager(txConfig)
    }

    override fun getObject(): SqlMapClient? = sqlMapClient

    override fun getObjectType(): Class<out SqlMapClient> = sqlMapClient?.javaClass ?: SqlMapClient::class.java

    override fun isSingleton(): Boolean = true
}
