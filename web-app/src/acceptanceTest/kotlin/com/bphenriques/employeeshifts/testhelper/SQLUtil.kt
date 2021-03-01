package com.bphenriques.employeeshifts.testhelper

import org.springframework.jdbc.core.JdbcTemplate

object SQLUtil {
    fun clearAll(jdbcTemplate: JdbcTemplate) {
        this.employee(jdbcTemplate).clear()
        this.shift(jdbcTemplate).clear()
    }
}

class EmployeeTableUtil(private val jdbcTemplate: JdbcTemplate) {

    fun clear() {
        jdbcTemplate.execute("DELETE FROM employee;")
    }
}

class ShiftTableUtil(private val jdbcTemplate: JdbcTemplate) {

    fun clear() {
        jdbcTemplate.execute("DELETE FROM shift;")
    }
}

// Accessors for API convenience and scope.
fun SQLUtil.employee(client: JdbcTemplate): EmployeeTableUtil = EmployeeTableUtil(client)
fun SQLUtil.shift(client: JdbcTemplate): ShiftTableUtil = ShiftTableUtil(client)
