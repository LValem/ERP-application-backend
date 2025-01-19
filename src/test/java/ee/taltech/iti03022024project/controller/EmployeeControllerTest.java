package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.WrongValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllEmployeesShouldReturnEmployeesList() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].employeeId", is(notNullValue())))
                .andExpect(jsonPath("$[0].name", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getEmployeeByIdShouldReturnEmployeeWhenExists() throws Exception {
        int employeeId = 2;
        mockMvc.perform(get("/api/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(employeeId)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.permissionId", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getEmployeeByIdShouldReturnNotFoundWhenEmployeeDoesNotExist() throws Exception {
        int nonExistentEmployeeId = 999;
        mockMvc.perform(get("/api/employees/{id}", nonExistentEmployeeId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createEmployeeShouldSucceed() throws Exception {
        String employeeJson = """
            {
                "name": "New Employee",
                "password": "securePassword123",
                "permissionId": 1
            }
        """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(notNullValue())))
                .andExpect(jsonPath("$.name", is("New Employee")))
                .andExpect(jsonPath("$.permissionId", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createEmployeeShouldFailWhenEmployeeAlreadyExists() throws Exception {
        String existingEmployeeJson = """
            {
                "name": "test",
                "password": "securePassword123",
                "permissionId": 1
            }
        """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(existingEmployeeJson))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals("Employee with name test already exists.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateEmployeeShouldFailWhenEmployeeDoesNotExist() throws Exception {
        int nonExistentEmployeeId = 999;
        String updateEmployeeJson = """
            {
                "name": "Non-Existent Employee",
                "permissionId": 1,
                "password": "newSecurePassword"
            }
        """;

        mockMvc.perform(put("/api/employees/{id}", nonExistentEmployeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEmployeeJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldReturnPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.currentPage", is(0)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldFilterByName() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("employeeName", "test")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].employeeName", containsStringIgnoringCase("test")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldFilterByCertificationNames() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("certificationNames", "B")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldReturnEmptyForNonExistentCertificationNames() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("certificationNames", "XYZ")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", empty()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldFilterByLastJobDateRange() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("lastJobStartDate", "2023-01-01T00:00:00")
                        .param("lastJobEndDate", "2025-12-31T23:59:59")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].lastJobDate", greaterThanOrEqualTo("2025-01-01T00:00:00")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldReturnEmptyForLastJobDateOutOfRange() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("lastJobStartDate", "2024-01-01T00:00:00")
                        .param("lastJobEndDate", "2024-12-31T23:59:59")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", empty()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldSortByLastJobDateAscending() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("sortBy", "lastJobDate")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].lastJobDate", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldSortByCertificationNamesAscending() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("sortBy", "certificationNames")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].certificationNames", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldSortByCertificationNamesDescending() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("sortBy", "certificationNames")
                        .param("sortDirection", "DESC")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].certificationNames", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldFilterByLastJobStartDateOnly() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("lastJobStartDate", "2024-01-18T00:00:00")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].lastJobDate", everyItem(greaterThanOrEqualTo("2025-01-18T00:00:00"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldFilterByLastJobEndDateOnly() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("lastJobEndDate", "2025-01-19T23:59:59")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].lastJobDate", everyItem(lessThanOrEqualTo("2025-01-19T23:59:59"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldSortByLastJobDateDesc() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("sortBy", "lastJobDate")
                        .param("sortDirection", "DESC")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loginShouldSucceedWithValidCredentials() throws Exception {
        String loginRequest = """
        {
            "name": "test",
            "password": "123456"
        }
    """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void loginShouldFailWithInvalidCredentials() throws Exception {
        String loginRequest = """
        {
            "name": "test",
            "password": "wrongPassword"
        }
    """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchEmployeesShouldSortByEmployeeNameAsc() throws Exception {
        mockMvc.perform(get("/api/employees/table")
                        .param("sortBy", "employeeName")
                        .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].employeeId", is(2)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateEmployeeShouldSucceedWithAllFields() throws Exception {
        int employeeId = 2;
        String updateEmployeeJson = """
        {
            "name": "Updated Name",
            "permissionId": 1,
            "password": "newSecurePassword"
        }
    """;

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEmployeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(employeeId)))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.permissionId", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateEmployeeShouldFailWhenNameAlreadyExists() throws Exception {
        int employeeId = 2;
        String updateEmployeeJson = """
        {
            "name": "test",
            "permissionId": 1,
            "password": "newSecurePassword"
        }
    """;

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEmployeeJson))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals("Cannot change name to test ,because test already exists!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateEmployeeShouldFailForInvalidPermissionId() throws Exception {
        int employeeId = 2;
        String updateEmployeeJson = """
        {
            "name": "Valid Name",
            "permissionId": 99,
            "password": "securePassword123"
        }
    """;

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEmployeeJson))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof WrongValueException))
                .andExpect(result -> assertEquals("99 is not a real PermissionID!",
                        result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateEmployeeShouldSucceedWithOnlyName() throws Exception {
        int employeeId = 2;
        String updateEmployeeJson = """
        {
            "name": "New Partial Name"
        }
    """;

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEmployeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(employeeId)))
                .andExpect(jsonPath("$.name", is("New Partial Name")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateEmployeeShouldSucceedWithOnlyPermissionId() throws Exception {
        int employeeId = 2;
        String updateEmployeeJson = """
        {
            "permissionId": 3
        }
    """;

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEmployeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(employeeId)))
                .andExpect(jsonPath("$.permissionId", is(3)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateEmployeeShouldSucceedWithOnlyPassword() throws Exception {
        int employeeId = 2;
        String updateEmployeeJson = """
        {
            "password": "anotherSecurePassword"
        }
    """;

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEmployeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId", is(employeeId)));
    }
}
