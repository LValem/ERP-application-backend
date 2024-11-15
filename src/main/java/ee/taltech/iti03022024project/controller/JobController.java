package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.JobDto;
import ee.taltech.iti03022024project.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    @PutMapping("/{id}")
    public ResponseEntity<JobDto> updateJob(@PathVariable Integer id, @RequestBody JobDto jobDto) {
        jobDto.setJobId(id);
        return jobService.updateJob(id, jobDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
