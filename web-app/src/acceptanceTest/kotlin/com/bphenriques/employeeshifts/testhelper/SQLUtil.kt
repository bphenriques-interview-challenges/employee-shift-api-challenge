package com.bphenriques.employeeshifts.testhelper

import org.springframework.r2dbc.core.DatabaseClient

object SQLUtil {
    fun clearAll(client: DatabaseClient) {
        this.employee(client).clear()
        this.shift(client).clear()
    }
}

class EmployeeTableUtil(private val client: DatabaseClient) {
    fun clear() {
        client.sql("DELETE FROM employee;").fetch().one().block()
    }
}
fun SQLUtil.employee(client: DatabaseClient): EmployeeTableUtil = EmployeeTableUtil(client)

class ShiftTableUtil(private val client: DatabaseClient) {
    fun clear() {
        client.sql("DELETE FROM shift;").fetch().one().block()
    }
}
fun SQLUtil.shift(client: DatabaseClient): ShiftTableUtil = ShiftTableUtil(client)
