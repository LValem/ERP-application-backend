package ee.taltech.iti03022024project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024project.AbstractIntegrationTest;
import ee.taltech.iti03022024project.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllOrdersShouldReturnOrdersList() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].orderId", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getOrderByIdShouldReturnOrderWhenExists() throws Exception {
        int orderId = 1;
        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(orderId)))
                .andExpect(jsonPath("$.customerId", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getOrderByIdShouldReturnNotFoundForInvalidId() throws Exception {
        int nonExistentOrderId = 999;
        mockMvc.perform(get("/api/orders/{id}", nonExistentOrderId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createOrderShouldSucceed() throws Exception {
        OrderDto orderDto = new OrderDto(null, 1,
                LocalDateTime.of(2025, 1, 20, 10, 0),
                LocalDateTime.of(2025, 1, 20, 18, 0),
                12000, 200, 300, 400, "Easy access");

        String orderRequest = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(orderRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(notNullValue())))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.weight", is(12000)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createOrderShouldFailForInvalidCustomerId() throws Exception {
        OrderDto orderDto = new OrderDto(null, 999,
                LocalDateTime.of(2025, 1, 20, 10, 0),
                LocalDateTime.of(2025, 1, 20, 18, 0),
                12000, 200, 300, 400, "Handle with care");

        String orderRequest = objectMapper.writeValueAsString(orderDto);

        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(orderRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateOrderShouldSucceedWhenOrderExists() throws Exception {
        OrderDto updatedOrder = new OrderDto(1, 1,
                LocalDateTime.of(2025, 1, 18, 14, 30),
                LocalDateTime.of(2025, 1, 19, 10, 0),
                6000, 400, 300, 900,
                "Updated order details.");

        String requestBody = objectMapper.writeValueAsString(updatedOrder);

        mockMvc.perform(put("/api/orders/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(1)))
                .andExpect(jsonPath("$.weight", is(6000)))
                .andExpect(jsonPath("$.orderDetails", is("Updated order details.")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateOrderShouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        OrderDto updatedOrder = new OrderDto(999, 1,
                LocalDateTime.of(2025, 1, 18, 14, 30),
                LocalDateTime.of(2025, 1, 19, 10, 0),
                6000, 400, 300, 900,
                "Updated order details.");

        String requestBody = objectMapper.writeValueAsString(updatedOrder);

        mockMvc.perform(put("/api/orders/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateOrderShouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        OrderDto updatedOrder = new OrderDto(1, 999,
                LocalDateTime.of(2025, 1, 18, 14, 30),
                LocalDateTime.of(2025, 1, 19, 10, 0),
                6000, 400, 300, 900,
                "Updated order details.");

        String requestBody = objectMapper.writeValueAsString(updatedOrder);

        mockMvc.perform(put("/api/orders/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateOrderShouldSucceedWithPartialData() throws Exception {
        OrderDto updatedOrder = new OrderDto();
        updatedOrder.setWeight(7000);
        updatedOrder.setOrderDetails("Partially updated details.");

        String requestBody = objectMapper.writeValueAsString(updatedOrder);

        mockMvc.perform(put("/api/orders/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(1)))
                .andExpect(jsonPath("$.weight", is(7000)))
                .andExpect(jsonPath("$.orderDetails", is("Partially updated details.")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableWithoutCriteria() throws Exception {
        mockMvc.perform(get("/api/orders/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].orderId", is(2)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldSortByOrderIdAsc() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("sortBy", "orderId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].orderId", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByPickupDateBetween() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("pickupStartDate", "2023-01-18T00:00:00")
                        .param("pickupEndDate", "2025-01-19T23:59:59")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].pickupDate", everyItem(allOf(
                        greaterThanOrEqualTo("2023-01-18T00:00:00"),
                        lessThanOrEqualTo("2025-01-19T23:59:59")
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByWeightRange() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("minWeight", "1000")
                        .param("maxWeight", "10000")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].weight", everyItem(allOf(
                        greaterThanOrEqualTo(1000),
                        lessThanOrEqualTo(10000)
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByDropOffDateStartOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("dropOffStartDate", "2023-01-18T00:00:00")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].dropOffDate", everyItem(greaterThanOrEqualTo("2023-01-18T00:00:00"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByDropOffDateEndOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("dropOffEndDate", "2025-01-19T23:59:59")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].dropOffDate", everyItem(lessThanOrEqualTo("2025-01-19T23:59:59"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersShouldFilterByDropOffDateBetween() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("dropOffStartDate", "2023-01-18T14:00:00")
                        .param("dropOffEndDate", "2025-01-19T12:00:00")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].dropOffDate", everyItem(allOf(
                        greaterThanOrEqualTo("2023-01-18T14:00:00"),
                        lessThanOrEqualTo("2025-01-19T12:00:00")
                ))));
    }


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByMinWeightOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("minWeight", "3000")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].weight", everyItem(greaterThanOrEqualTo(3000))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByMaxWeightOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("maxWeight", "15000")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].weight", everyItem(lessThanOrEqualTo(15000))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByMinLengthOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("minLength", "100")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].length", everyItem(greaterThanOrEqualTo(100))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByMaxLengthOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("maxLength", "1000")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].length", everyItem(lessThanOrEqualTo(1000))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByMinWidthOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("minWidth", "200")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].width", everyItem(greaterThanOrEqualTo(200))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByMaxWidthOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("maxWidth", "800")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].width", everyItem(lessThanOrEqualTo(800))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByMinHeightOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("minHeight", "100")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].height", everyItem(greaterThanOrEqualTo(100))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByMaxHeightOnly() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("maxHeight", "400")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].height", everyItem(lessThanOrEqualTo(400))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByLengthRange() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("minLength", "200")
                        .param("maxLength", "1000")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].length", everyItem(allOf(
                        greaterThanOrEqualTo(200),
                        lessThanOrEqualTo(1000)
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByWidthRange() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("minWidth", "300")
                        .param("maxWidth", "800")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].width", everyItem(allOf(
                        greaterThanOrEqualTo(300),
                        lessThanOrEqualTo(800)
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByHeightRange() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("minHeight", "200")
                        .param("maxHeight", "500")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].height", everyItem(allOf(
                        greaterThanOrEqualTo(200),
                        lessThanOrEqualTo(500)
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersTableShouldFilterByCustomerName() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("customerName", "John")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].customerName", everyItem(containsStringIgnoringCase("John Doe Inc."))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchOrdersShouldSortByCustomerName() throws Exception {
        mockMvc.perform(get("/api/orders/table")
                        .param("sortBy", "customerName")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].customerName", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getOrdersWithoutJobShouldReturnOrders() throws Exception {
        mockMvc.perform(get("/api/orders/without-job"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].orderId", notNullValue()))
                .andExpect(jsonPath("$[0].customerName", is("John Doe Inc.")));
    }
}
