package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.JobDto;
import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.query.DoneJobTableInfoDto;
import ee.taltech.iti03022024project.dto.query.NotDoneJobTableInfoDto;
import ee.taltech.iti03022024project.dto.searchcriteria.DoneJobSearchCriteria;
import ee.taltech.iti03022024project.dto.searchcriteria.NotDoneJobSearchCriteria;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.entity.JobEntity;
import ee.taltech.iti03022024project.entity.OrderEntity;
import ee.taltech.iti03022024project.entity.VehicleEntity;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.JobMapping;
import ee.taltech.iti03022024project.repository.EmployeeRepository;
import ee.taltech.iti03022024project.repository.JobRepository;
import ee.taltech.iti03022024project.repository.OrderRepository;
import ee.taltech.iti03022024project.repository.VehicleRepository;
import ee.taltech.iti03022024project.repository.specifications.DoneJobSpecifications;
import ee.taltech.iti03022024project.repository.specifications.NotDoneJobSpecifications;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class JobService {

    private final JobRepository jobRepository;
    private final VehicleRepository vehicleRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;
    private final JobMapping jobMapping;

    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    private static final String DOES_NOT_EXIST = " does not exist.";

    public JobDto createJob(JobDto jobDto) {
        System.out.println("vehicle id" + jobDto.getVehicleId());
        System.out.println("job id" + jobDto.getEmployeeId());
        System.out.println("order id" + jobDto.getOrderId());
        VehicleEntity vehicle = vehicleRepository.findById(jobDto.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle with ID " + jobDto.getVehicleId() + DOES_NOT_EXIST));
        EmployeeEntity employee = employeeRepository.findById(jobDto.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee with ID " + jobDto.getEmployeeId() + DOES_NOT_EXIST));
        OrderEntity order = orderRepository.findById(jobDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order with ID " + jobDto.getOrderId() + DOES_NOT_EXIST));

        JobEntity jobEntity = jobMapping.jobToEntity(jobDto);
        jobEntity.setVehicle(vehicle);
        jobEntity.setEmployee(employee);
        jobEntity.setOrder(order);

        if (jobEntity.getIsComplete() == null) {
            jobEntity.setIsComplete(false);
        }

        JobEntity savedJob = jobRepository.save(jobEntity);

        log.info("Created job with ID: {}", savedJob.getJobId());

        return jobMapping.jobToDto(savedJob);
    }

    public List<JobDto> getAllJobs() {
        List<JobEntity> jobs = jobRepository.findAll();
        log.info("Fetched all jobs, count: {}", jobs.size());
        return jobMapping.jobListToDtoList(jobs);
    }

    public Optional<JobDto> getJobById(Integer id) {
        JobEntity jobEntity = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job with ID " + id + DOES_NOT_EXIST));

        log.info("Fetched job with ID: {}", id);

        return Optional.of(jobMapping.jobToDto(jobEntity));
    }

    public Optional<JobDto> updateJob(Integer id, JobDto jobDto) {
        JobEntity jobEntity = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job with ID " + id + DOES_NOT_EXIST));

        if (jobDto.getVehicleId() != null) {
            VehicleEntity vehicle = vehicleRepository.findById(jobDto.getVehicleId())
                    .orElseThrow(() -> new NotFoundException("Vehicle with ID " + jobDto.getVehicleId() + DOES_NOT_EXIST));
            jobEntity.setVehicle(vehicle);
        }
        if (jobDto.getEmployeeId() != null) {
            EmployeeEntity employee = employeeRepository.findById(jobDto.getEmployeeId())
                    .orElseThrow(() -> new NotFoundException("Employee with ID " + jobDto.getEmployeeId() + DOES_NOT_EXIST));
            jobEntity.setEmployee(employee);
        }
        if (jobDto.getOrderId() != null) {
            OrderEntity order = orderRepository.findById(jobDto.getOrderId())
                    .orElseThrow(() -> new NotFoundException("Order with ID " + jobDto.getOrderId() + DOES_NOT_EXIST));
            jobEntity.setOrder(order);
        }
        if (jobDto.getPickupDate() != null) {
            jobEntity.setPickupDate(jobDto.getPickupDate());
        }
        if (jobDto.getDropOffDate() != null) {
            jobEntity.setDropOffDate(jobDto.getDropOffDate());
        }
        if (jobDto.getIsComplete() != null) {
            jobEntity.setIsComplete(jobDto.getIsComplete());
        }

        JobEntity updatedJob = jobRepository.save(jobEntity);

        log.info("Updated job with ID: {}", id);

        return Optional.of(jobMapping.jobToDto(updatedJob));
    }

    public PageResponse<DoneJobTableInfoDto> searchDoneJobsTable(DoneJobSearchCriteria criteria) {
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "jobId";
        switch (sortBy) {
            case "vehicleId": sortBy = "vehicle.vehicleId"; break;
            case "registrationPlate": sortBy = "vehicle.registrationPlate"; break;
            case "fuelUsed": sortBy = "fc.fuelUsed"; break;
            case "distanceDriven": sortBy = "fc.distanceDriven"; break;
            case "orderId": sortBy = "order.orderId"; break;
            case "customerName": sortBy = "order.customer.name"; break;
            default: break;
        }

        String direction = criteria.getSortDirection() != null ? criteria.getSortDirection().toUpperCase() : "DESC";
        int pageNumber = criteria.getPage() != null ? criteria.getPage() : 0;
        int pageSize = criteria.getSize() != null ? criteria.getSize() : 20;

        // Create Sort and Pageable
        Sort sort = Sort.by(Sort.Direction.valueOf(direction), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Build the specification
        Specification<JobEntity> spec = Specification
                .where(DoneJobSpecifications.jobId(criteria.getJobId()))
                .and(DoneJobSpecifications.vehicleId(criteria.getVehicleId()))
                .and(DoneJobSpecifications.registrationPlateLike(criteria.getRegistrationPlate()))
                .and(DoneJobSpecifications.orderId(criteria.getOrderId()))
                .and(DoneJobSpecifications.customerNameLike(criteria.getCustomerName()))
                .and(DoneJobSpecifications.pickupDateBetween(criteria.getPickupStartDate(), criteria.getPickupEndDate()))
                .and(DoneJobSpecifications.dropOffDateBetween(criteria.getDropOffStartDate(), criteria.getDropOffEndDate()))
                .and(DoneJobSpecifications.fuelUsedBetween(criteria.getMinFuelUsed(), criteria.getMaxFuelUsed()))
                .and((root, query, builder) -> builder.isTrue(root.get("isComplete")));

        // Execute query with pagination
        Page<DoneJobTableInfoDto> dtoPage = jobRepository.findAllDoneJobTableInfo(spec, pageable);
        log.info("Fetched {} done jobs based on search criteria.", dtoPage.getTotalElements());
        return new PageResponse<>(dtoPage);
    }

    public PageResponse<NotDoneJobTableInfoDto> searchNotDoneJobsTable(NotDoneJobSearchCriteria criteria) {
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "jobId";
        switch (sortBy) {
            case "vehicleId": sortBy = "vehicle.vehicleId"; break;
            case "registrationPlate": sortBy = "vehicle.registrationPlate"; break;
            case "orderId": sortBy = "order.orderId"; break;
            case "customerName": sortBy = "order.customer.name"; break;
            default: break;
        }

        String direction = criteria.getSortDirection() != null ? criteria.getSortDirection().toUpperCase() : "DESC";
        int pageNumber = criteria.getPage() != null ? criteria.getPage() : 0;
        int pageSize = criteria.getSize() != null ? criteria.getSize() : 20;

        // Create Sort and Pageable
        Sort sort = Sort.by(Sort.Direction.valueOf(direction), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Build the specification
        Specification<JobEntity> spec = Specification
                .where(NotDoneJobSpecifications.jobId(criteria.getJobId()))
                .and(NotDoneJobSpecifications.vehicleId(criteria.getVehicleId()))
                .and(NotDoneJobSpecifications.registrationPlateLike(criteria.getRegistrationPlate()))
                .and(NotDoneJobSpecifications.orderId(criteria.getOrderId()))
                .and(NotDoneJobSpecifications.customerNameLike(criteria.getCustomerName()))
                .and(NotDoneJobSpecifications.pickupDateBetween(criteria.getPickupStartDate(), criteria.getPickupEndDate()))
                .and(NotDoneJobSpecifications.dropOffDateBetween(criteria.getDropOffStartDate(), criteria.getDropOffEndDate()))
                .and((root, query, builder) -> builder.isFalse(root.get("isComplete")));

        // Execute query with pagination
        Page<NotDoneJobTableInfoDto> dtoPage = jobRepository.findAllNotDoneJobTableInfo(spec, pageable);
        log.info("Fetched {} not done jobs based on search criteria.", dtoPage.getTotalElements());
        return new PageResponse<>(dtoPage);
    }
}
