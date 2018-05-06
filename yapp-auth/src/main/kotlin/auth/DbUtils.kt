package auth

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SchemaUtils.withDataBaseLock
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import javax.sql.DataSource

object DbUtils {
    private lateinit var dataSource: DataSource

    private val log = LoggerFactory.getLogger(this::class.java)
    private const val DEFAULT_JDBC_URL: String =
        "jdbc:postgresql://localhost:5432/postgres?user=root&password=root&ssl=false"

    fun init() {
        log.info("Start db init script")
        setupConnectionPool()
        launchMigrations()
        log.info("Db init completed")
    }

    private fun setupConnectionPool() {
        val config = HikariConfig()
        config.jdbcUrl = System.getenv("JDBC_URL") ?: DEFAULT_JDBC_URL
        config.maximumPoolSize = 10

        dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }

    private fun launchMigrations() {
        transaction {
            withDataBaseLock {
                SchemaUtils.createMissingTablesAndColumns(UsersTable)
            }
        }
    }
}
