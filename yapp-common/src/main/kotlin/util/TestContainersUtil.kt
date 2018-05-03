package util

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

class KPostgreSQLContainer(imageName: String) : PostgreSQLContainer<KPostgreSQLContainer>(imageName)
