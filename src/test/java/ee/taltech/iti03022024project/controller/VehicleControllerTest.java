package ee.taltech.iti03022024project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024project.AbstractIntegrationTest;
import ee.taltech.iti03022024project.dto.VehicleDto;
import ee.taltech.iti03022024project.dto.searchcriteria.VehicleSearchCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithMaxLoadThatMatchesSomeVehicles() throws Exception {
        // Test: maxLoad with some vehicles matching the condition
        mockMvc.perform(get("/api/vehicles/table")
                        .param("maximumLoad", "50000")  // Vehicles with load <= 50000 should match
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].registrationPlate", is("123ABC")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getAllVehiclesShouldReturnVehiclesList() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].vehicleId", is(notNullValue())))
                .andExpect(jsonPath("$[0].registrationPlate", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getVehicleByIdShouldReturnVehicleWhenExists() throws Exception {
        int vehicleId = 1;
        mockMvc.perform(get("/api/vehicles/{id}", vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId", is(vehicleId)))
                .andExpect(jsonPath("$.registrationPlate", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getVehicleByIdShouldReturnNotFoundWhenDoesNotExist() throws Exception {
        int vehicleId = 9999; // assuming this ID does not exist
        mockMvc.perform(get("/api/vehicles/{id}", vehicleId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createVehicleShouldReturnCreatedVehicle() throws Exception {
        VehicleDto vehicleDto = new VehicleDto(null, 'T', true, 1000, 500, "CBA123");

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId", is(notNullValue())))
                .andExpect(jsonPath("$.registrationPlate", is("CBA123")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateVehicleShouldReturnUpdatedVehicle() throws Exception {
        int vehicleId = 1; // assuming this ID exists
        VehicleDto vehicleDto = new VehicleDto(null, 'V', false, 1500, 700, "XYZ789");

        mockMvc.perform(put("/api/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId", is(vehicleId)))
                .andExpect(jsonPath("$.registrationPlate", is("XYZ789")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void updateVehicleShouldReturnNotFoundWhenDoesNotExist() throws Exception {
        int vehicleId = 9999; // assuming this ID does not exist
        VehicleDto vehicleDto = new VehicleDto(null, 'V', false, 1500, 700, "XYZ789");

        mockMvc.perform(put("/api/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithSpecificCriteriaShouldReturnFilteredResults() throws Exception {
        mockMvc.perform(get("/api/vehicles/table")
                        .param("vehicleType", "K")  // Vehicle with type "K"
                        .param("isInUse", "false")  // Vehicles that are not in use
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].vehicleType", is("K")))
                .andExpect(jsonPath("$.content[0].isInUse", is(false)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithFuelCriteriaShouldReturnCorrectFilteredResults() throws Exception {
        mockMvc.perform(get("/api/vehicles/table")
                        .param("minFuel", "100")  // Vehicles with at least 100 fuel
                        .param("maxFuel", "400")  // Vehicles with at most 400 fuel
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].currentFuel", greaterThanOrEqualTo(100)))
                .andExpect(jsonPath("$.content[0].currentFuel", lessThanOrEqualTo(400)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithInvalidRegistrationPlateShouldReturnFilteredResults() throws Exception {
        mockMvc.perform(get("/api/vehicles/table")
                        .param("registrationPlate", "123ABC")  // Searching by registration plate "123ABC"
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].registrationPlate", containsString("123ABC")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithRangeCriteriaShouldReturnCorrectFilteredResults() throws Exception {
        mockMvc.perform(get("/api/vehicles/table")
                        .param("minimumLoad", "5000")  // Vehicles with at least 5000 load
                        .param("maximumLoad", "60000")  // Vehicles with at most 60000 load
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].maxLoad", greaterThanOrEqualTo(5000)))
                .andExpect(jsonPath("$.content[0].maxLoad", lessThanOrEqualTo(60000)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithMinFuelOnlyShouldReturnVehiclesWithFuelGreaterThanOrEqualToMin() throws Exception {
        mockMvc.perform(get("/api/vehicles/table")
                        .param("minFuel", "200")  // Vehicles with at least 200 fuel
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].currentFuel", greaterThanOrEqualTo(200)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithMaxFuelOnlyShouldReturnVehiclesWithFuelLessThanOrEqualToMax() throws Exception {
        mockMvc.perform(get("/api/vehicles/table")
                        .param("maxFuel", "300")  // Vehicles with at most 300 fuel
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].currentFuel", lessThanOrEqualTo(300)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithBothMinAndMaxFuelShouldReturnVehiclesWithinFuelRange() throws Exception {
        mockMvc.perform(get("/api/vehicles/table")
                        .param("minFuel", "150")  // Vehicles with at least 150 fuel
                        .param("maxFuel", "400")  // Vehicles with at most 400 fuel
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].currentFuel", greaterThanOrEqualTo(150)))
                .andExpect(jsonPath("$.content[0].currentFuel", lessThanOrEqualTo(400)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithNoMinOrMaxFuelShouldReturnAllVehicles() throws Exception {
        mockMvc.perform(get("/api/vehicles/table")
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].currentFuel", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithMinLoadOnlyShouldReturnVehiclesWithLoadGreaterThanOrEqualToMin() throws Exception {
        // Test: minimumLoad is provided, maximumLoad is null
        mockMvc.perform(get("/api/vehicles/table")
                        .param("minLoad", "30000")  // Vehicles with at least 30000 load
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].maxLoad", greaterThanOrEqualTo(30000)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithMaxLoadOnlyShouldReturnVehiclesWithLoadLessThanOrEqualToMax() throws Exception {
        // Test: maximumLoad is provided, minimumLoad is null
        mockMvc.perform(get("/api/vehicles/table")
                        .param("maxLoad", "50000")  // Vehicles with at most 50000 load
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].maxLoad", lessThanOrEqualTo(50000)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithBothMinAndMaxLoadShouldReturnVehiclesWithinLoadRange() throws Exception {
        // Test: both minimumLoad and maximumLoad are provided (load between 20000 and 50000)
        mockMvc.perform(get("/api/vehicles/table")
                        .param("minLoad", "20000")  // Vehicles with at least 20000 load
                        .param("maxLoad", "50000")  // Vehicles with at most 50000 load
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].maxLoad", greaterThanOrEqualTo(20000)))
                .andExpect(jsonPath("$.content[0].maxLoad", lessThanOrEqualTo(50000)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithNoMinOrMaxLoadShouldReturnAllVehicles() throws Exception {
        // Test: both minimumLoad and maximumLoad are null, no filtering
        mockMvc.perform(get("/api/vehicles/table")
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.content[0].maxLoad", is(notNullValue())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithMinLoadOnlyThatDoesNotExist() throws Exception {
        // Test: minLoad with no vehicles matching the condition
        mockMvc.perform(get("/api/vehicles/table")
                        .param("minimumLoad", "100000")  // No vehicle should have load >= 100000
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", empty()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithMaxLoadOnlyThatDoesNotExist() throws Exception {
        // Test: maxLoad with no vehicles matching the condition
        mockMvc.perform(get("/api/vehicles/table")
                        .param("maximumLoad", "10000")  // No vehicle should have load <= 10000
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", empty()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithBothMinAndMaxLoadWhereNoVehiclesMatch() throws Exception {
        // Test: both minLoad and maxLoad are provided but no vehicles match the condition
        mockMvc.perform(get("/api/vehicles/table")
                        .param("minimumLoad", "100000")  // No vehicle should have load >= 100000
                        .param("maximumLoad", "10000")   // No vehicle should have load <= 10000
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", empty()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void searchVehiclesWithMinLoadThatMatchesSomeVehicles() throws Exception {
        // Test: minLoad with some vehicles matching the condition
        mockMvc.perform(get("/api/vehicles/table")
                        .param("minimumLoad", "30000")  // Vehicles with load >= 30000 should match
                        .param("sortBy", "vehicleId")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))  // Expecting 3 vehicles with load >= 30000
                .andExpect(jsonPath("$.content[0].registrationPlate", is("123ABC")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getVehicleByIdShouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {
        int vehicleId = 9999; // assuming this ID does not exist
        mockMvc.perform(get("/api/vehicles/{id}", vehicleId))
                .andExpect(status().isNotFound())  // Expect 404 Not Found
                .andExpect(jsonPath("$", is("There is no vehicle with id 9999"))); // Example message for EntityNotFoundException
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void createVehicleShouldReturnBadRequestWhenInvalidData() throws Exception {
        VehicleDto vehicleDto = new VehicleDto(null, 'T', true, 1000, 500, "123CBA"); // Invalid load value

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$", is("Vehicle with registration plate 123CBA already exists.")));
    }

}
