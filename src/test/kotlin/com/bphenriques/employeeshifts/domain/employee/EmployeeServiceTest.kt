package com.bphenriques.employeeshifts.domain.employee

import com.bphenriques.employeeshifts.domain.employee.model.Employee
import com.bphenriques.employeeshifts.domain.employee.model.EmployeeNotFoundException
import com.bphenriques.employeeshifts.domain.employee.service.EmployeeService
import com.bphenriques.employeeshifts.infrastructure.psql.EmployeeRepository
import com.bphenriques.employeeshifts.infrastructure.psql.EmployeeRow
import com.bphenriques.employeeshifts.testhelper.Generator.randomLong
import com.bphenriques.employeeshifts.testhelper.Generator.uuid
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EmployeeServiceTest {

    private val repository = mockk<EmployeeRepository>().apply {
        coEvery { save(any()) } answers { (it.invocation.args.first() as EmployeeRow).copy(id = randomLong()) }
    }
    private val subject = EmployeeService(
        repository = repository
    )

    @Test
    fun `saveEmployee - It saves the employee and returns it`() = runBlocking {
        val employee = Employee(
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        val expectedEmployeeRow = EmployeeRow(
            id = 0L,
            firstName = employee.firstName,
            lastName = employee.lastName,
            address = employee.address
        )

        val result = subject.saveEmployee(employee)

        Assertions.assertEquals(employee, result)
        coVerify { repository.save(expectedEmployeeRow) }
        confirmVerified(repository)
    }

    @Test
    fun `getEmployee - Returns the employee when it exists`() = runBlocking {
        val employee = Employee(
            firstName = uuid(),
            lastName = uuid(),
            address = uuid()
        )
        val expectedEmployeeRow = EmployeeRow(
            id = 0L,
            firstName = employee.firstName,
            lastName = employee.lastName,
            address = employee.address
        )

        val result = subject.saveEmployee(employee)

        Assertions.assertEquals(employee, result)
        coVerify { repository.save(expectedEmployeeRow) }
        confirmVerified(repository)
    }

    @Test
    fun `getEmployee - It throws EmployeeNotFoundException when the employee does not exists`() = runBlocking {
        val employeeId = randomLong()
        coEvery { repository.findById(any()) } returns null

        val thrownException = assertThrows<EmployeeNotFoundException> {
            subject.getEmployee(employeeId)
        }

        Assertions.assertEquals(employeeId, thrownException.employeeId)
        coVerify { repository.findById(employeeId) }
        confirmVerified(repository)
    }
}
