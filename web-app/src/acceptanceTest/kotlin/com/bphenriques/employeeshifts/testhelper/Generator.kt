package com.bphenriques.employeeshifts.testhelper

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.test.Generator.uuid

fun newEmployee() = Employee(
    id = 0,
    firstName = uuid(),
    lastName = uuid(),
    address = uuid()
)
