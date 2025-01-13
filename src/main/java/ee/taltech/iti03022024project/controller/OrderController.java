package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.OrderDto;
import ee.taltech.iti03022024project.dto.OrderNameIdDto;
import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.query.OrdersTableInfoDto;
import ee.taltech.iti03022024project.dto.searchcriteria.OrderSearchCriteria;
import ee.taltech.iti03022024project.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create a new order",
            description = "Adds a new order based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Order created successfully")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        OrderDto createdOrder = orderService.createOrder(orderDto);
        return ResponseEntity.ok(createdOrder);
    }

    @Operation(
            summary = "Get all orders",
            description = "Fetches all orders and returns a list of order DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "404", description = "There are no orders")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Get an order by ID",
            description = "Retrieves an order by its ID and returns the corresponding order DTO."
    )
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Order with this ID does not exist")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Integer id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update an existing order",
            description = "Updates an existing order's details based on the provided information."
    )
    @ApiResponse(responseCode = "200", description = "Order updated successfully")
    @ApiResponse(responseCode = "404", description = "Order or customer not found")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Integer id, @RequestBody OrderDto orderDto) {
        orderDto.setOrderId(id);
        return orderService.updateOrder(id, orderDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Search for orders in table view",
            description = "Search orders by criteria, sort, and return a paginated table view"
    )
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "404", description = "There are no orders")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @GetMapping("/table")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<PageResponse<OrdersTableInfoDto>> searchOrdersTable(@Valid OrderSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new OrderSearchCriteria();
        }
        PageResponse<OrdersTableInfoDto> response = orderService.searchOrdersTable(criteria);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search for orders that don't have a job associated with them",
            description = "Search orders that don't have a job associated with them"
    )
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @ApiResponse(responseCode = "404", description = "There are no orders")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @GetMapping("/without-job")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public ResponseEntity<List<OrderNameIdDto>> getOrdersWithoutJob() {
        List<OrderNameIdDto> orders = orderService.getOrdersWithoutJob();
        return ResponseEntity.ok(orders);
    }
}
