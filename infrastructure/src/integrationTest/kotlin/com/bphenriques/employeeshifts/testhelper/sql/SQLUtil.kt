package com.bphenriques.employeeshifts.testhelper.sql

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

    fun count(): Int = jdbcTemplate.queryForObject("SELECT COUNT (*) FROM employee;", Int::class.java) ?: error("Unexpected failed result")
}

class ShiftTableUtil(private val jdbcTemplate: JdbcTemplate) {

    fun clear() {
        jdbcTemplate.execute("DELETE FROM shift;")
    }

    fun count(): Int = jdbcTemplate.queryForObject("SELECT COUNT (*) FROM shift;", Int::class.java) ?: error("Unexpected failed result")
}

// Accessors for API convenience and scope.
fun SQLUtil.employee(client: JdbcTemplate): EmployeeTableUtil = EmployeeTableUtil(client)
fun SQLUtil.shift(client: JdbcTemplate): ShiftTableUtil = ShiftTableUtil(client)
