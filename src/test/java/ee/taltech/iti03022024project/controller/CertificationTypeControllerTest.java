package ee.taltech.iti03022024project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024project.AbstractIntegrationTest;
import ee.taltech.iti03022024project.dto.CertificationTypeDto;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
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
class CertificationTypeControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createCertificationTypeShouldAddNewType() throws Exception {
        CertificationTypeDto newType = new CertificationTypeDto(null, "D");

        mockMvc.perform(post("/api/certification-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationName", is("D")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createCertificationTypeShouldThrowConflictWhenDuplicateName() throws Exception {
        CertificationTypeDto newType = new CertificationTypeDto(null, "Duplicate Certification");

        mockMvc.perform(post("/api/certification-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationName", is("Duplicate Certification")));

        mockMvc.perform(post("/api/certification-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newType)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals(
                        "CertificationType with name Duplicate Certification already exists.",
                        result.getResolvedException().getMessage()
                ));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllCertificationTypesShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/certification-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getCertificationTypeByIdShouldReturnType() throws Exception {
        int certificationTypeId = 1;

        mockMvc.perform(get("/api/certification-types/{id}", certificationTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationName", is("B")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getCertificationTypeByIdShouldReturnNotFoundWhenTypeDoesNotExist() throws Exception {
        int nonExistentId = 999;

        mockMvc.perform(get("/api/certification-types/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateCertificationTypeShouldUpdateType() throws Exception {
        int certificationTypeId = 2;
        CertificationTypeDto updatedType = new CertificationTypeDto(certificationTypeId, "Updated Type C");

        mockMvc.perform(put("/api/certification-types/{id}", certificationTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedType)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationName", is("Updated Type C")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateCertificationTypeShouldThrowConflictWhenNameExists() throws Exception {
        CertificationTypeDto updatedType = new CertificationTypeDto(1, "C");

        mockMvc.perform(put("/api/certification-types/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedType)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals(
                        "Cannot change name to C, because it already exists!",
                        result.getResolvedException().getMessage()
                ));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCertificationTypesShouldReturnMatchingResults() throws Exception {
        mockMvc.perform(get("/api/certification-types/table")
                        .param("certificationName", "B")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].certificationName", is("B")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchCertificationTypesShouldHandleNullCriteria() throws Exception {
        mockMvc.perform(get("/api/certification-types/table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));
    }
}
