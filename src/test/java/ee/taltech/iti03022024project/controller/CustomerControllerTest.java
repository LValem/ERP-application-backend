package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024project.dto.CustomerDto;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser(username = "test")
    void getCustomer() throws Exception {
        mockMvc.perform(get("/api/customers/table")
                .param("page", "0")
                .param("size", "20"))
        .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllCustomersShouldReturnAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("John Doe Inc."))
                .andExpect(jsonPath("$[1].name").value("Acme Corp."))
                .andExpect(jsonPath("$[2].name").value("Globex Ltd."));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void getCustomerByIdShouldReturnCustomerWhenExists() throws Exception {
        int customerId = 1;
        mockMvc.perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe Inc."))
                .andExpect(jsonPath("$.address").value("123 Elm Street"))
                .andExpect(jsonPath("$.cityCounty").value("Tallinn"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    void getCustomerByIdShouldReturnNotFoundWhenDoesNotExist() throws Exception {
        int nonExistentCustomerId = 999;

        mockMvc.perform(get("/api/customers/{id}", nonExistentCustomerId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createCustomerShouldCreateNewCustomer() throws Exception {
        CustomerDto newCustomer = new CustomerDto(
                null, "New Customer", "456 Test Avenue", "Pärnu",
                "67890", "new.customer@example.com", "+37256789012", "EE567890123"
        );

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Customer"))
                .andExpect(jsonPath("$.address").value("456 Test Avenue"))
                .andExpect(jsonPath("$.cityCounty").value("Pärnu"));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[3].name").value("New Customer"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateCustomerShouldUpdateExistingCustomer() throws Exception {
        int customerId = 2;
        CustomerDto updatedCustomer = new CustomerDto(
                customerId, "Updated Acme Corp", "Updated Address", "Updated City",
                "20221", "updated@acme.com", "+372111222333", "EE123987654"
        );

        mockMvc.perform(put("/api/customers/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Acme Corp"))
                .andExpect(jsonPath("$.address").value("Updated Address"))
                .andExpect(jsonPath("$.email").value("updated@acme.com"));

        mockMvc.perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Acme Corp"))
                .andExpect(jsonPath("$.address").value("Updated Address"))
                .andExpect(jsonPath("$.email").value("updated@acme.com"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createCustomerShouldThrowConflictWhenNameAlreadyExists() throws Exception {
        CustomerDto duplicateCustomer = new CustomerDto(
                null, "John Doe Inc.", "123 Test Street", "City",
                "11111", "another@example.com", "+37212345678", "EE123456789");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCustomer)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals(
                        "Customer with name John Doe Inc. already exists.",
                        result.getResolvedException().getMessage()
                ));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateCustomerShouldThrowConflictWhenNameAlreadyExists() throws Exception {
        CustomerDto updateToDuplicate = new CustomerDto(
                2, "John Doe Inc.", "Updated Address", "Updated City",
                "33333", "updated@example.com", "+37233333333", "EE333333333"
        );

        mockMvc.perform(put("/api/customers/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateToDuplicate)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals(
                        "Customer with name John Doe Inc. already exists.",
                        result.getResolvedException().getMessage()
                ));
    }


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCustomerTableShouldReturnMatchingResults() throws Exception {
        mockMvc.perform(get("/api/customers/table")
                        .param("page", "0")
                        .param("size", "20")
                        .param("customerName", "John Doe Inc."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].customerName").value("John Doe Inc."));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCustomerTableShouldHandleNullCriteria() throws Exception {
        mockMvc.perform(get("/api/customers/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[*].customerId", containsInAnyOrder(1, 2, 3)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCustomerTableShouldHandleEmptyCustomerName() throws Exception {
        mockMvc.perform(get("/api/customers/table")
                        .param("customerName", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCustomerTableShouldFilterByAddress() throws Exception {
        mockMvc.perform(get("/api/customers/table")
                        .param("address", "123 Elm Street"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.content[0].address").value("123 Elm Street"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCustomerTableShouldFilterByZip() throws Exception {
        mockMvc.perform(get("/api/customers/table")
                        .param("zip", "10115")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.content[*].zip", everyItem(equalTo("10115"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCustomerTableShouldFilterByLastOrderDateRange() throws Exception {
        mockMvc.perform(get("/api/customers/table")
                        .param("startLastOrderDate", "2023-01-01T00:00:00")
                        .param("endLastOrderDate", "2023-12-31T23:59:59")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.content", not(empty())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCustomerTableShouldSortByLastOrderDateDescending() throws Exception {
        mockMvc.perform(get("/api/customers/table")
                        .param("sortBy", "lastOrderDate")
                        .param("sortDirection", "DESC")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0)).
                andExpect(jsonPath("$.content[0].lastOrderDate").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCustomerTableShouldSortByLastOrderDateAscending() throws Exception {
        mockMvc.perform(get("/api/customers/table")
                        .param("sortBy", "lastOrderDate")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.content[0].lastOrderDate").value("2025-01-19T10:00:00"));

    }
}
