package com.bphenriques.employeeshifts.infrastructure.psql

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EmployeeRepository : CoroutineCrudRepository<EmployeeRow, Int>
