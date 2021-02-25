package com.bphenriques.employeeshifts.testhelper

import com.bphenriques.employeeshifts.domain.employee.model.Employee

object JsonFixture {

    val employee = EmployeeJsonFixture()

    class EmployeeJsonFixture {
        fun request(employee: Employee) = """
            {
                "first_name": "${employee.firstName}",
                "last_name": "${employee.lastName}",
                "address": "${employee.address}"
            }
        """.trimIndent()

        fun response(employee: Employee) = """
            {
                "first_name": "${employee.firstName}",
                "last_name": "${employee.lastName}",
                "address": "${employee.address}"
            }
        """.trimIndent()
    }
}
