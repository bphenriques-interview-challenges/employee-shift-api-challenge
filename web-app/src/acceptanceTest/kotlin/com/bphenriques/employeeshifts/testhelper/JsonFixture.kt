package com.bphenriques.employeeshifts.testhelper

import com.bphenriques.employeeshifts.domain.employee.model.Employee

object JsonFixture {

    val employee = EmployeeJsonFixture()

    class EmployeeJsonFixture {
        fun createRequest(employee: Employee) = """
            {
                "first_name": "${employee.firstName}",
                "last_name": "${employee.lastName}",
                "address": "${employee.address}"
            }
        """.trimIndent()

        fun createResponse(employeeId: Int, employee: Employee) = """
            {
                "id": $employeeId,
                "first_name": "${employee.firstName}",
                "last_name": "${employee.lastName}",
                "address": "${employee.address}"
            }
        """.trimIndent()
    }
}
