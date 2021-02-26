package com.bphenriques.employeeshifts.domain

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.repository.DomainEmployeeRepository
import com.bphenriques.employeeshifts.domain.employee.service.EmployeeService
import com.bphenriques.test.Generator.randomInt
import com.bphenriques.test.Generator.uuid
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EmployeeServiceTest {

    private val fixedEmployeeId = randomInt()
    private val repository = mockk<DomainEmployeeRepository>()
    private val subject = EmployeeService(
        repository = repository
    )

    @Test
    fun `createEmployee - It returns the entity returned by DomainEmployeeRepository`() = runBlocking {
        val employee = Employee(
            id = 0,
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )

        coEvery { repository.create(employee) } returns employee.copy(fixedEmployeeId)

        val result = subject.createEmployee(employee)

        Assertions.assertEquals(employee.copy(id = fixedEmployeeId), result)
        coVerify { repository.create(employee) }
        confirmVerified(repository)
    }

    @Test
    fun `getEmployee - Returns the employee returned by the repository`() = runBlocking {
        val savedEmployee = Employee(
            id = fixedEmployeeId,
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )

        coEvery { repository.get(fixedEmployeeId) } returns savedEmployee

        val result = subject.getEmployee(fixedEmployeeId)

        Assertions.assertEquals(savedEmployee, result)
        coVerify { repository.get(fixedEmployeeId) }
        confirmVerified(repository)
    }
}
