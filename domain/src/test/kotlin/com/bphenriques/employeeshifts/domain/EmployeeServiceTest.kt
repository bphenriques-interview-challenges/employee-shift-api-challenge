package com.bphenriques.employeeshifts.domain

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.service.EmployeeService
import com.bphenriques.employeeshifts.infrastructure.psql.EmployeeRepository
import com.bphenriques.employeeshifts.infrastructure.psql.EmployeeRow
import com.bphenriques.test.Generator.randomInt
import com.bphenriques.test.Generator.uuid
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EmployeeServiceTest {

    private val fixedEmployeeId = randomInt()
    private val repository = mockk<EmployeeRepository>().apply {
        coEvery { save(any()) } answers { (it.invocation.args.first() as EmployeeRow).copy(id = fixedEmployeeId) }
    }
    private val subject = EmployeeService(
        repository = repository
    )

    @Test
    fun `createEmployee - It saves the employee and returns it`() = runBlocking {
        val employee = Employee(
            id = 0,
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )

        val result = subject.createEmployee(employee)

        Assertions.assertEquals(employee.copy(id = fixedEmployeeId), result)
        coVerify { repository.save(EmployeeRow(0, employee.firstName, employee.lastName, employee.address)) }
        confirmVerified(repository)
    }

    @Test
    fun `getEmployee - Returns the employee when it exists`() = runBlocking {
        val expectedResult = Employee(
            id = fixedEmployeeId,
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        val savedEmployee = EmployeeRow(fixedEmployeeId, expectedResult.firstName, expectedResult.lastName, expectedResult.address)

        coEvery { repository.findById(fixedEmployeeId) } returns savedEmployee

        val result = subject.getEmployee(fixedEmployeeId)

        Assertions.assertEquals(expectedResult, result)
        coVerify { repository.findById(fixedEmployeeId) }
        confirmVerified(repository)
    }

    @Test
    fun `getEmployee - It throws EmployeeNotFoundException when the employee does not exists`() = runBlocking {
        val employeeId = randomInt()
        coEvery { repository.findById(any()) } returns null

        val thrownException = assertThrows<EmployeeNotFoundException> {
            subject.getEmployee(employeeId)
        }

        Assertions.assertEquals(employeeId, thrownException.employeeId)
        coVerify { repository.findById(employeeId) }
        confirmVerified(repository)
    }
}
