package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.JobDto;
import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.query.DoneJobTableInfoDto;
import ee.taltech.iti03022024project.dto.query.NotDoneJobTableInfoDto;
import ee.taltech.iti03022024project.dto.searchcriteria.DoneJobSearchCriteria;
import ee.taltech.iti03022024project.dto.searchcriteria.NotDoneJobSearchCriteria;
import ee.taltech.iti03022024project.dto.searchcriteria.OrderSearchCriteria;
import ee.taltech.iti03022024project.service.JobService;
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
@RequestMapping("/api/jobs")
@Tag(name = "Jobs", description = "APIs for managing jobs")
public class JobController {

    private final JobService jobService;

    @Operation(
            summary = "Create a new job",
            description = "Adds a new job based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Job created successfully")
    @ApiResponse(responseCode = "404", description = "Related entity (vehicle, employee, or order) not found")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<JobDto> createJob(@RequestBody JobDto jobDto) {
        JobDto createdJob = jobService.createJob(jobDto);
        return ResponseEntity.ok(createdJob);
    }

    @Operation(
            summary = "Get all jobs",
            description = "Fetches all jobs and returns a list of job DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'DRIVER')")
    @GetMapping
    public ResponseEntity<List<JobDto>> getAllJobs() {
        List<JobDto> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @Operation(
            summary = "Get a job by ID",
            description = "Retrieves a job by its ID and returns the corresponding job DTO."
    )
    @ApiResponse(responseCode = "200", description = "Job retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Job with this ID does not exist")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'DRIVER')")
    @GetMapping("/{id}")
    public ResponseEntity<JobDto> getJobById(@PathVariable Integer id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update an existing job",
            description = "Updates an existing job's details based on the provided information."
    )
    @ApiResponse(responseCode = "200", description = "Job updated successfully")
    @ApiResponse(responseCode = "404", description = "Job or related entity not found")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'DRIVER')")
    @PutMapping("/{id}")
    public ResponseEntity<JobDto> updateJob(@PathVariable Integer id, @RequestBody JobDto jobDto) {
        jobDto.setJobId(id);
        return jobService.updateJob(id, jobDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Search for done jobs",
            description = "Fetches a paginated and filtered list of done jobs based on search criteria."
    )
    @ApiResponse(responseCode = "200", description = "Done jobs retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PostMapping("/done-table")
    public ResponseEntity<PageResponse<DoneJobTableInfoDto>> searchDoneJobs(@Valid @RequestBody(required = false) DoneJobSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new DoneJobSearchCriteria();
        }
        PageResponse<DoneJobTableInfoDto> response = jobService.searchDoneJobsTable(criteria);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Search for not done jobs",
            description = "Fetches a paginated and filtered list of not done jobs based on search criteria."
    )
    @ApiResponse(responseCode = "200", description = "Not done jobs retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PostMapping("/not-done-table")
    public ResponseEntity<PageResponse<NotDoneJobTableInfoDto>> searchNotDoneJobs(@Valid @RequestBody(required = false) NotDoneJobSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new NotDoneJobSearchCriteria();
        }
        PageResponse<NotDoneJobTableInfoDto> response = jobService.searchNotDoneJobsTable(criteria);
        return ResponseEntity.ok(response);
    }
}
