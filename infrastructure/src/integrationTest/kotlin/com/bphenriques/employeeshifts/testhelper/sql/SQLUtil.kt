package com.bphenriques.employeeshifts.testhelper.sql

import org.springframework.r2dbc.core.DatabaseClient

object SQLUtil {
    fun clearAll(client: DatabaseClient) {
        this.employee(client).clear()
    }
}

class EmployeeTableUtil(private val client: DatabaseClient) {

    fun clear() {
        client.sql("DELETE FROM employee;").fetch().one().block()
    }

    fun count(): Long = client.sql("SELECT COUNT (*) FROM employee;")
        .map { row -> row.get(0) as Long }
        .first()
        .block() ?: error("Unexpected error executing query")
}

// Accessors for API convenience and scope.
fun SQLUtil.employee(client: DatabaseClient): EmployeeTableUtil = EmployeeTableUtil(client)
