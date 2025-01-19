package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createEmployeeShouldCreateNewEmployee() throws Exception {
        String newEmployeeJson = """
                {
                    "name": "New Employee",
                    "password": "password123",
                    "permissionId": 1
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmployeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Employee"))
                .andExpect(jsonPath("$.permissionId").value(1));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Adjust size based on existing test data
                .andExpect(jsonPath("$[1].name").value("New Employee"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createEmployeeShouldThrowConflictWhenNameAlreadyExists() throws Exception {
        String duplicateEmployeeJson = """
                {
                    "name": "test",
                    "password": "password123",
                    "permissionId": 1
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateEmployeeJson))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals(
                        "Employee with name test already exists.",
                        result.getResolvedException().getMessage()
                ));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateEmployeeShouldUpdateExistingEmployee() throws Exception {
        String updatedEmployeeJson = """
                {
                    "name": "Updated Employee",
                    "permissionId": 2,
                    "password": "newpassword123"
                }
                """;

        mockMvc.perform(put("/api/employees/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedEmployeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Employee"))
                .andExpect(jsonPath("$.permissionId").value(2));

        mockMvc.perform(get("/api/employees/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Employee"))
                .andExpect(jsonPath("$.permissionId").value(2));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateEmployeeShouldThrowConflictWhenNameAlreadyExists() throws Exception {
        String duplicateNameJson = """
                {
                    "name": "test",
                    "permissionId": 2,
                    "password": "password123"
                }
                """;

        mockMvc.perform(put("/api/employees/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateNameJson))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals(
                        "Cannot change name to test ,because test already exists!",
                        result.getResolvedException().getMessage()
                ));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getEmployeeByIdShouldReturnEmployeeWhenExists() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.permissionId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getEmployeeByIdShouldReturnNotFoundWhenDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllEmployeesShouldReturnAllEmployees() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].name").value("test"));
    }
}
