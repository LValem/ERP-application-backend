package ee.taltech.iti03022024project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024project.AbstractIntegrationTest;
import ee.taltech.iti03022024project.dto.JobDto;
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
class JobControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllJobsShouldReturnJobsList() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].jobId", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getJobByIdShouldReturnJobWhenExists() throws Exception {
        int jobId = 1;
        mockMvc.perform(get("/api/jobs/{id}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId", is(jobId)))
                .andExpect(jsonPath("$.employeeId", is(notNullValue())))
                .andExpect(jsonPath("$.orderId", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getJobByIdShouldReturnNotFoundWhenJobDoesNotExist() throws Exception {
        int nonExistentJobId = 999;
        mockMvc.perform(get("/api/jobs/{id}", nonExistentJobId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createJobShouldSucceed() throws Exception {
        JobDto jobDto = new JobDto(null, 1, 1, 1,
                LocalDateTime.of(2023, 1, 1, 10, 10),
                LocalDateTime.of(2023, 1, 1, 12, 10), false);

        String jobRequest = objectMapper.writeValueAsString(jobDto);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId", is(notNullValue())))
                .andExpect(jsonPath("$.employeeId", is(1)))
                .andExpect(jsonPath("$.orderId", is(1)))
                .andExpect(jsonPath("$.isComplete", is(false)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createJobShouldFailWhenEmployeeDoesNotExist() throws Exception {
        JobDto jobDto = new JobDto(null, 1, 999, 1,
                LocalDateTime.of(2023, 1, 1, 10, 10),
                LocalDateTime.of(2023, 1, 1, 12, 10), false);

        String jobRequest = objectMapper.writeValueAsString(jobDto);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createJobShouldFailWhenOrderDoesNotExist() throws Exception {
        JobDto jobDto = new JobDto(null, 1, 1, 999,
                LocalDateTime.of(2023, 1, 1, 10, 10),
                LocalDateTime.of(2023, 1, 1, 12, 10), false);

        String jobRequest = objectMapper.writeValueAsString(jobDto);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateJobShouldSucceedWhenJobExists() throws Exception {
        int jobId = 1;
        JobDto updatedJobDto = new JobDto(null, 1, 1, 1,
                LocalDateTime.of(2024, 1, 1, 10, 10),
                LocalDateTime.of(2024, 1, 1, 12, 10), false);

        String jobRequest = objectMapper.writeValueAsString(updatedJobDto);

        mockMvc.perform(put("/api/jobs/{id}", jobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId", is(jobId)))
                .andExpect(jsonPath("$.isComplete", is(false)))
                .andExpect(jsonPath("$.dropOffDate", is("2024-01-01T12:10:00")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateJobShouldReturnNotFoundWhenJobDoesNotExist() throws Exception {
        int nonExistentJobId = 999;
        JobDto jobDto = new JobDto(null, 1, 1, 1,
                LocalDateTime.of(2024, 1, 1, 10, 10),
                LocalDateTime.of(2024, 1, 1, 12, 10), false);

        String jobRequest = objectMapper.writeValueAsString(jobDto);

        mockMvc.perform(put("/api/jobs/{id}", nonExistentJobId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createJobWithoutIsCompleteShouldSucceed() throws Exception {
        JobDto jobDto = new JobDto(null, 1, 1, 1,
                LocalDateTime.of(2023, 1, 1, 10, 10),
                LocalDateTime.of(2023, 1, 1, 12, 10), null);

        String jobRequest = objectMapper.writeValueAsString(jobDto);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId", is(notNullValue())))
                .andExpect(jsonPath("$.employeeId", is(1)))
                .andExpect(jsonPath("$.orderId", is(1)))
                .andExpect(jsonPath("$.isComplete", is(false)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldReturnPaginatedResults() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].jobId", everyItem(notNullValue())))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.currentPage", is(0)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByJobId() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("jobId", "2")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].jobId", is(2)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByDateRange() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("startDate", "2023-01-01T00:00:00")
                        .param("endDate", "2023-12-31T23:59:59")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].completionDate",
                        everyItem(greaterThanOrEqualTo("2023-01-01T00:00:00"))))
                .andExpect(jsonPath("$.content[*].completionDate",
                        everyItem(lessThanOrEqualTo("2023-12-31T23:59:59"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldReturnEmptyWhenNoMatches() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("jobId", "999")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", empty()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldHandleNullCriteria() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.currentPage", is(0)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByVehicleId() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("vehicleId", "1")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].vehicleId", everyItem(is(1))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByRegistrationPlate() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("registrationPlate", "123ABC")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].registrationPlate", everyItem(containsString("123ABC"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByCustomerName() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("customerName", "John Doe Inc.")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].customerName", everyItem(containsString("John Doe Inc."))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByFuelUsedRange() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("minFuelUsed", "10.0")
                        .param("maxFuelUsed", "50.0")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].fuelUsed", everyItem(allOf(
                        greaterThanOrEqualTo(10.0),
                        lessThanOrEqualTo(50.0)
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByDistanceDrivenRange() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("minDistanceDriven", "40.0")
                        .param("maxDistanceDriven", "80.0")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].distanceDriven", everyItem(allOf(
                        greaterThanOrEqualTo(40.0),
                        lessThanOrEqualTo(80.0)
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByPickupDateStartOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("pickupStartDate", "2023-01-01T00:00:00")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].pickupDate", everyItem(greaterThanOrEqualTo("2025-01-01T00:00:00"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByPickupDateBetween() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("pickupStartDate", "2023-01-18T00:00:00")
                        .param("pickupEndDate", "2025-01-18T23:59:59")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].pickupDate", everyItem(allOf(
                        greaterThanOrEqualTo("2023-01-18T00:00:00"),
                        lessThanOrEqualTo("2025-01-18T23:59:59")
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByPickupDateEndOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("pickupEndDate", "2025-12-31T23:59:59")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].pickupDate", everyItem(lessThanOrEqualTo("2025-12-31T23:59:59"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByDropOffDateStartOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("dropOffStartDate", "2023-01-01T00:00:00")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].dropOffDate", everyItem(greaterThanOrEqualTo("2025-01-01T00:00:00"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByDropOffDateBetween() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("dropOffStartDate", "2023-01-18T00:00:00")
                        .param("dropOffEndDate", "2025-01-19T23:59:59")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].dropOffDate", everyItem(allOf(
                        greaterThanOrEqualTo("2023-01-18T00:00:00"),
                        lessThanOrEqualTo("2025-01-19T23:59:59")
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByDropOffDateEndOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("dropOffEndDate", "2025-12-31T23:59:59")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].dropOffDate", everyItem(lessThanOrEqualTo("2025-12-31T23:59:59"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByMinFuelUsedOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("minFuelUsed", "4.0")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].fuelUsed", everyItem(greaterThanOrEqualTo(4.0))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByMaxFuelUsedOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("maxFuelUsed", "150.0")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].fuelUsed", everyItem(lessThanOrEqualTo(150.0))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByMinDistanceDrivenOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("minDistanceDriven", "40.0")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].distanceDriven", everyItem(greaterThanOrEqualTo(40.0))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldFilterByMaxDistanceDrivenOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("maxDistanceDriven", "80.0")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].distanceDriven", everyItem(lessThanOrEqualTo(80.0))));
    }


    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldSortByVehicleId() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].vehicleId", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldSortByRegistrationPlate() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("sortBy", "registrationPlate")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].registrationPlate", is("123ABC")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldSortByFuelUsed() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("sortBy", "fuelUsed")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].fuelUsed", is(20.0)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldSortByDistanceDriven() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("sortBy", "distanceDriven")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].distanceDriven", is(50.0)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldSortByOrderId() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("sortBy", "orderId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].orderId", is(2)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchDoneJobsShouldSortByCustomerName() throws Exception {
        mockMvc.perform(get("/api/jobs/done-table")
                        .param("sortBy", "customerName")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].customerName", is("John Doe Inc.")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldFilterByPickupDateBetween() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
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
    void searchNotDoneJobsShouldFilterByDropOffDateBetween() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("dropOffStartDate", "2023-01-18T00:00:00")
                        .param("dropOffEndDate", "2025-01-19T23:59:59")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].dropOffDate", everyItem(allOf(
                        greaterThanOrEqualTo("2023-01-18T00:00:00"),
                        lessThanOrEqualTo("2025-01-19T23:59:59")
                ))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldFilterByPickupStartDateOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("pickupStartDate", "2023-01-18T00:00:00")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].pickupDate", everyItem(greaterThanOrEqualTo("2025-01-18T00:00:00"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldFilterByPickupEndDateOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("pickupEndDate", "2025-01-19T23:59:59")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].pickupDate", everyItem(lessThanOrEqualTo("2025-01-19T23:59:59"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldFilterByDropOffStartDateOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("dropOffStartDate", "2023-01-18T00:00:00")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].dropOffDate", everyItem(greaterThanOrEqualTo("2025-01-18T00:00:00"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldFilterByDropOffEndDateOnly() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("dropOffEndDate", "2025-01-19T23:59:59")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].dropOffDate", everyItem(lessThanOrEqualTo("2025-01-19T23:59:59"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldFilterByJobId() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("jobId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].jobId", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldFilterByVehicleId() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("vehicleId", "1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].vehicleId", everyItem(is(1))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldFilterByRegistrationPlate() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("registrationPlate", "123ABC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].registrationPlate", everyItem(containsString("123ABC"))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldFilterByCustomerName() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("customerName", "John Doe Inc.")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[*].customerName", everyItem(containsString("John Doe Inc."))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldSortByJobId() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("sortBy", "jobId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].jobId", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldSortByVehicleId() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].vehicleId", is(1)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldSortByRegistrationPlate() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("sortBy", "registrationPlate")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].registrationPlate", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldSortByOrderId() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
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
    void searchNotDoneJobsShouldSortByCustomerName() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
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
    void searchNotDoneJobsShouldSortByPickupDate() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("sortBy", "pickupDate")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].pickupDate", notNullValue()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchNotDoneJobsShouldSortByDropOffDate() throws Exception {
        mockMvc.perform(get("/api/jobs/not-done-table")
                        .param("sortBy", "dropOffDate")
                        .param("sortDirection", "DESC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].dropOffDate", notNullValue()));
    }
}
