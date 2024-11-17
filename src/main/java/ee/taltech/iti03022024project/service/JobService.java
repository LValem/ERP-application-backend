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

    public JobDto createJob(JobDto jobDto) {
        VehicleEntity vehicle = vehicleRepository.findById(jobDto.getVehicleId())
                .orElseThrow(() -> new NotFoundException("Vehicle with ID " + jobDto.getVehicleId() + " does not exist."));
        EmployeeEntity employee = employeeRepository.findById(jobDto.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee with ID " + jobDto.getEmployeeId() + " does not exist."));
        OrderEntity order = orderRepository.findById(jobDto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order with ID " + jobDto.getOrderId() + " does not exist."));

        JobEntity jobEntity = jobMapping.jobToEntity(jobDto);
        jobEntity.setVehicle(vehicle);
        jobEntity.setEmployee(employee);
        jobEntity.setOrder(order);

        JobEntity savedJob = jobRepository.save(jobEntity);
        return jobMapping.jobToDto(savedJob);
    }

    public List<JobDto> getAllJobs() {
        List<JobEntity> jobs = jobRepository.findAll();
        return jobMapping.jobListToDtoList(jobs);
    }

    public Optional<JobDto> getJobById(Integer id) {
        JobEntity jobEntity = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job with ID " + id + " does not exist."));
        return Optional.of(jobMapping.jobToDto(jobEntity));
    }

    public Optional<JobDto> updateJob(Integer id, JobDto jobDto) {
        JobEntity jobEntity = jobRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Job with ID " + id + " does not exist."));

        if (jobDto.getVehicleId() != null) {
            VehicleEntity vehicle = vehicleRepository.findById(jobDto.getVehicleId())
                    .orElseThrow(() -> new NotFoundException("Vehicle with ID " + jobDto.getVehicleId() + " does not exist."));
            jobEntity.setVehicle(vehicle);
        }
        if (jobDto.getEmployeeId() != null) {
            EmployeeEntity employee = employeeRepository.findById(jobDto.getEmployeeId())
                    .orElseThrow(() -> new NotFoundException("Employee with ID " + jobDto.getEmployeeId() + " does not exist."));
            jobEntity.setEmployee(employee);
        }
        if (jobDto.getOrderId() != null) {
            OrderEntity order = orderRepository.findById(jobDto.getOrderId())
                    .orElseThrow(() -> new NotFoundException("Order with ID " + jobDto.getOrderId() + " does not exist."));
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
        return Optional.of(jobMapping.jobToDto(updatedJob));
    }

    public PageResponse<DoneJobTableInfoDto> searchDoneJobsTable(DoneJobSearchCriteria criteria) {
        // Default sorting and pagination
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "jobId";
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
                .and(DoneJobSpecifications.fuelUsedBetween(criteria.getMinFuelUsed(), criteria.getMaxFuelUsed()));

        // Execute query with pagination
        Page<JobEntity> jobPage = jobRepository.findAll(spec, pageable);

        // Map Page<JobEntity> to Page<DoneJobTableInfoDto>
        Page<DoneJobTableInfoDto> dtoPage = jobPage.map(job -> new DoneJobTableInfoDto(
                job.getJobId(),
                job.getVehicle().getVehicleId(),
                job.getVehicle().getRegistrationPlate(),
                job.getFuelConsumption() != null ? job.getFuelConsumption().getFuelUsed() : null,
                job.getFuelConsumption() != null ? job.getFuelConsumption().getDistanceDriven() : null,
                job.getOrder().getOrderId(),
                job.getOrder().getCustomer().getName(),
                job.getPickupDate(),
                job.getDropOffDate(),
                job.getIsComplete()
        ));

        // Return a PageResponse wrapping the Page<DoneJobTableInfoDto>
        return new PageResponse<>(dtoPage);
    }

    public PageResponse<NotDoneJobTableInfoDto> searchNotDoneJobsTable(NotDoneJobSearchCriteria criteria) {
        // Default sorting and pagination
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "jobId";
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
                .and(NotDoneJobSpecifications.dropOffDateBetween(criteria.getDropOffStartDate(), criteria.getDropOffEndDate()));

        // Execute query with pagination
        Page<JobEntity> jobPage = jobRepository.findAll(spec, pageable);

        // Map Page<JobEntity> to Page<NotDoneJobTableInfoDto>
        Page<NotDoneJobTableInfoDto> dtoPage = jobPage.map(job -> new NotDoneJobTableInfoDto(
                job.getJobId(),
                job.getVehicle().getVehicleId(),
                job.getVehicle().getRegistrationPlate(),
                job.getOrder().getOrderId(),
                job.getOrder().getCustomer().getName(),
                job.getPickupDate(),
                job.getDropOffDate(),
                job.getIsComplete()
        ));

        // Return a PageResponse wrapping the Page<NotDoneJobTableInfoDto>
        return new PageResponse<>(dtoPage);
    }
}
