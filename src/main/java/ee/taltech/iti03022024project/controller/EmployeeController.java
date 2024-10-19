package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.EmployeeDto;
import ee.taltech.iti03022024project.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "Employee management APIs")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(
            summary = "Add a new employee to the system",
            description = "Creates a new employee based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Employee added successfully")
    @ApiResponse(responseCode = "409", description = "Employee with this name already exists")
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
        return ResponseEntity.ok(createdEmployee);
    }

    @Operation(
            summary = "Get all employees from the system",
            description = "Fetches all employees and returns a list of employee DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @ApiResponse(responseCode = "404", description = "There are no employees")
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Operation(
            summary = "Get an employee by ID",
            description = "Retrieves an employee by its ID and returns the corresponding employee DTO."
    )
    @ApiResponse(responseCode = "200", description = "Employee retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Employee with this ID does not exist")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
