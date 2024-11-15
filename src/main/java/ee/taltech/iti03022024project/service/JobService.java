package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.JobDto;
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
import lombok.RequiredArgsConstructor;
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
}
