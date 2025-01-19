package ee.taltech.iti03022024project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024project.AbstractIntegrationTest;
import ee.taltech.iti03022024project.dto.CertificationDto;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CertificationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createCertificationShouldCreateNewCertification() throws Exception {
        CertificationDto newCertification = new CertificationDto(
                null, 1, 2, LocalDate.of(2022, 12, 2), LocalDate.of(2032, 12, 2)
        );

        mockMvc.perform(post("/api/certifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCertification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1))
                .andExpect(jsonPath("$.certificationTypeId").value(2))
                .andExpect(jsonPath("$.issuedDate").value("2022-12-02"))
                .andExpect(jsonPath("$.expiryDate").value("2032-12-02"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllCertificationsShouldReturnListOfCertifications() throws Exception {
        mockMvc.perform(get("/api/certifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getCertificationByIdShouldReturnCertificationWhenExists() throws Exception {
        int certificationId = 1;

        mockMvc.perform(get("/api/certifications/{id}", certificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationId").value(certificationId))
                .andExpect(jsonPath("$.employeeId").isNotEmpty())
                .andExpect(jsonPath("$.certificationTypeId").isNotEmpty())
                .andExpect(jsonPath("$.issuedDate").isNotEmpty())
                .andExpect(jsonPath("$.expiryDate").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getCertificationByIdShouldReturnNotFoundWhenDoesNotExist() throws Exception {
        int nonExistentCertificationId = 999;

        mockMvc.perform(get("/api/certifications/{id}", nonExistentCertificationId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateCertificationShouldUpdateExistingCertification() throws Exception {
        int certificationId = 1;
        CertificationDto updatedCertification = new CertificationDto(
                certificationId, 7, 2, LocalDate.of(2023, 1, 1), LocalDate.of(2033, 1, 1)
        );

        mockMvc.perform(put("/api/certifications/{id}", certificationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCertification)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationId").value(certificationId))
                .andExpect(jsonPath("$.certificationTypeId").value(2))
                .andExpect(jsonPath("$.issuedDate").value("2023-01-01"))
                .andExpect(jsonPath("$.expiryDate").value("2033-01-01"));

        mockMvc.perform(get("/api/certifications/{id}", certificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificationTypeId").value(2))
                .andExpect(jsonPath("$.issuedDate").value("2023-01-01"))
                .andExpect(jsonPath("$.expiryDate").value("2033-01-01"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createCertificationShouldThrowAlreadyExistsException() throws Exception {
        CertificationDto duplicateCertification = new CertificationDto(
                null, 1, 1, LocalDate.of(2022, 12, 2), LocalDate.of(2032, 12, 2)
        );
        mockMvc.perform(post("/api/certifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCertification)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals(
                        "Certification for this employee with the same type already exists.",
                        result.getResolvedException().getMessage()
                ));
    }
}
