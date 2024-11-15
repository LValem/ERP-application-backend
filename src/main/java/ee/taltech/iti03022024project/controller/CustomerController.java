package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.CustomerDto;
import ee.taltech.iti03022024project.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
            summary = "Create a new customer",
            description = "Adds a new customer based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Customer created successfully")
    @ApiResponse(responseCode = "409", description = "Customer with this name already exists")
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerDto createdCustomer = customerService.createCustomer(customerDto);
        return ResponseEntity.ok(createdCustomer);
    }

    @Operation(
            summary = "Get all customers",
            description = "Fetches all customers and returns a list of customer DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @Operation(
            summary = "Get a customer by ID",
            description = "Retrieves a customer by its ID and returns the corresponding customer DTO."
    )
    @ApiResponse(responseCode = "200", description = "Customer retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Customer with this ID does not exist")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Integer id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update an existing customer",
            description = "Updates an existing customer's details based on the provided information."
    )
    @ApiResponse(responseCode = "200", description = "Customer updated successfully")
    @ApiResponse(responseCode = "404", description = "Customer with this ID does not exist")
    @ApiResponse(responseCode = "409", description = "Customer with the new name already exists")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Integer id, @RequestBody CustomerDto customerDto) {
        customerDto.setCustomerId(id);
        return customerService.updateCustomer(customerDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
