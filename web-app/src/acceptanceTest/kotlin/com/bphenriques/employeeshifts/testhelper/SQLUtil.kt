package com.bphenriques.employeeshifts.testhelper

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
}

// Accessors for API convenience and scope.
fun SQLUtil.employee(client: DatabaseClient): EmployeeTableUtil = EmployeeTableUtil(client)
