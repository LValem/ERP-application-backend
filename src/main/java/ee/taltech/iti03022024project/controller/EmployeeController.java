package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.CreateEmployeeDto;
import ee.taltech.iti03022024project.dto.EmployeeDto;
import ee.taltech.iti03022024project.dto.LoginRequestDto;
import ee.taltech.iti03022024project.dto.LoginResponseDto;
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
    @PreAuthorize("hasAuthority('DEFAULT USER')")
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
    @GetMapping("/api/employees/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
