package com.bphenriques.api.core.employee.infrastructure.psql

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EmployeeRepository : CoroutineCrudRepository<EmployeeRow, Long>
