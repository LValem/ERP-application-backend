package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.AbstractIntegrationTest;
import ee.taltech.iti03022024project.dto.employee.PermissionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PermissionControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllPermissionsShouldReturnPermissions() throws Exception {
        mockMvc.perform(get("/api/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", instanceOf(List.class)))
                .andExpect(jsonPath("$", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is("ADMIN")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createPermissionShouldSucceed() throws Exception {
        PermissionDto permissionDto = new PermissionDto("USER");

        mockMvc.perform(post("/api/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("USER")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getPermissionByIdShouldReturnPermission() throws Exception {
        int permissionId = 1;

        mockMvc.perform(get("/api/permissions/{id}", permissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("ADMIN")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getPermissionByIdShouldReturnNotFoundWhenPermissionDoesNotExist() throws Exception {
        int nonExistentPermissionId = 999;

        mockMvc.perform(get("/api/permissions/{id}", nonExistentPermissionId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createPermissionShouldFailWhenPermissionAlreadyExists() throws Exception {
        PermissionDto duplicatePermission = new PermissionDto("ADMIN");
        String jsonRequest = objectMapper.writeValueAsString(duplicatePermission);

        mockMvc.perform(post("/api/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(content().string("Permission with name ADMIN already exists."));
    }

}
