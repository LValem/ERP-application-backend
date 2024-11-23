package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.employee.*;
import ee.taltech.iti03022024project.dto.query.EmployeeTableInfoDto;
import ee.taltech.iti03022024project.dto.searchcriteria.EmployeeSearchCriteria;
import ee.taltech.iti03022024project.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Employees", description = "Employee management APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(
            summary = "Add a new employee to the system",
            description = "Creates a new employee based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Employee added successfully")
    @ApiResponse(responseCode = "409", description = "Employee with this name already exists")
    @ApiResponse(responseCode = "409", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/api/employees")
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody CreateEmployeeDto createEmployeeDto) {
        EmployeeDto createdEmployee = employeeService.createEmployee(createEmployeeDto);
        return ResponseEntity.ok(createdEmployee);
    }

    @Operation(
            summary = "Get all employees from the system",
            description = "Fetches all employees and returns a list of employee DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @ApiResponse(responseCode = "404", description = "There are no employees")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/api/employees")
    public ResponseEntity<List<EmployeeDto>> getEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Operation(
            summary = "Get an employee by ID",
            description = "Retrieves an employee by its ID and returns the corresponding employee DTO."
    )
    @ApiResponse(responseCode = "200", description = "Employee retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Employee with this ID does not exist")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/api/employees/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update employee by ID",
            description = "Changes the employee with given ID"
    )
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @ApiResponse(responseCode = "404", description = "Employee with this ID doesn't exist!")
    @ApiResponse(responseCode = "409", description = "Name or password can't be null!")
    @ApiResponse(responseCode = "409", description = "Employee with this name already exists")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/api/employees/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Integer id,
                                                      @RequestBody UpdateEmployeeRequest request) {
        return employeeService.updateEmployee(id, request.getName(), request.getPermissionId(), request.getPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Search for employees in table view",
            description = "Search employees by criteria, sort, and return a paginated table view."
    )
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @ApiResponse(responseCode = "404", description = "There are no employees")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping("api/employees/table")
    public ResponseEntity<PageResponse<EmployeeTableInfoDto>> searchEmployees(@RequestBody(required = false) EmployeeSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new EmployeeSearchCriteria();
        }
        PageResponse<EmployeeTableInfoDto> response = employeeService.searchEmployeeTable(criteria);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Login",
            description = "Attempts to login with the provided credentials and generates token if login was successful"
    )
    @ApiResponse(responseCode = "200", description = "Employee login successful")
    @ApiResponse(responseCode = "401", description = "Employee login unsuccessful")

    @PostMapping("/api/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return employeeService.login(loginRequestDto);
    }
}
