package com.bphenriques.employeeshifts.testhelper.sql

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient

object SQLUtil

class EmployeeTableUtil(private val client: DatabaseClient) {

    fun clear() = client.sql("DELETE FROM employee;").fetch().one().block()

    fun count(): Long = client.sql("SELECT COUNT (*) FROM employee;")
        .map { row -> row.get(0) as Long }
        .first()
        .block() ?: error("Unexpected error executing query")
}

// Accessors for API convenience and scope.
fun SQLUtil.employee(template: R2dbcEntityTemplate): EmployeeTableUtil = EmployeeTableUtil(template.databaseClient)
