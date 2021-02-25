package com.bphenriques.api.infrastructure.psql.configuration

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Flyway is not yet fully compatible with R2DBC. Follows a manual setup.
 *
 * See: https://stackoverflow.com/questions/59553647/how-to-run-flyway-migration-for-reactive-r2dbc-driver-on-sprintboot-stratup
 */
@Configuration
class FlywayConfiguration(
    @Value("\${spring.flyway.url}") private val url: String,
    @Value("\${spring.flyway.user}") private val user: String,
    @Value("\${spring.flyway.password}") private val password: String,
    @Value("\${spring.flyway.locations}") private val locations: Array<String>
) {
    @Bean(initMethod = "migrate")
    fun flyway() = Flyway(
        Flyway.configure()
            .baselineOnMigrate(true)
            .dataSource(url, user, password)
            .locations(*locations)
    )
}
