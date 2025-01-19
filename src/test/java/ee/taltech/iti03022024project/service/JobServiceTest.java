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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private JobMapping jobMapping;

    @InjectMocks
    private JobService jobService;

    private JobDto jobDto;
    private JobEntity jobEntity;
    private VehicleEntity vehicle;
    private EmployeeEntity employee;
    private OrderEntity order;

    @BeforeEach
    void setUp() {
        jobDto = new JobDto();
        jobDto.setJobId(1);
        jobDto.setVehicleId(10);
        jobDto.setEmployeeId(20);
        jobDto.setOrderId(30);
        jobDto.setIsComplete(null);

        vehicle = new VehicleEntity();
        vehicle.setVehicleId(10);

        employee = new EmployeeEntity(20, "Alice", 2, "secret");

        order = new OrderEntity();
        order.setOrderId(30);

        jobEntity = new JobEntity();
        jobEntity.setJobId(1);
    }

    @Test
    void createJob_ShouldCreateWhenValid() {
        when(vehicleRepository.findById(10)).thenReturn(Optional.of(vehicle));
        when(employeeRepository.findById(20)).thenReturn(Optional.of(employee));
        when(orderRepository.findById(30)).thenReturn(Optional.of(order));
        when(jobMapping.jobToEntity(jobDto)).thenReturn(jobEntity);
        when(jobRepository.save(jobEntity)).thenReturn(jobEntity);
        when(jobMapping.jobToDto(jobEntity)).thenReturn(jobDto);

        JobDto result = jobService.createJob(jobDto);

        assertNotNull(result);
        assertEquals(1, result.getJobId());
        verify(jobRepository).save(jobEntity);
    }

    @Test
    void createJob_ShouldThrowNotFoundWhenVehicleMissing() {
        when(vehicleRepository.findById(10)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> jobService.createJob(jobDto));
        verify(jobRepository, never()).save(any());
    }

    @Test
    void createJob_ShouldThrowNotFoundWhenEmployeeMissing() {
        when(vehicleRepository.findById(10)).thenReturn(Optional.of(vehicle));
        when(employeeRepository.findById(20)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> jobService.createJob(jobDto));
        verify(jobRepository, never()).save(any());
    }

    @Test
    void createJob_ShouldThrowNotFoundWhenOrderMissing() {
        when(vehicleRepository.findById(10)).thenReturn(Optional.of(vehicle));
        when(employeeRepository.findById(20)).thenReturn(Optional.of(employee));
        when(orderRepository.findById(30)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> jobService.createJob(jobDto));
        verify(jobRepository, never()).save(any());
    }

    @Test
    void getAllJobs_ShouldReturnList() {
        when(jobRepository.findAll()).thenReturn(List.of(jobEntity));
        when(jobMapping.jobListToDtoList(List.of(jobEntity))).thenReturn(List.of(jobDto));

        List<JobDto> result = jobService.getAllJobs();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getJobId());
    }

    @Test
    void getJobById_ShouldReturnWhenFound() {
        when(jobRepository.findById(1)).thenReturn(Optional.of(jobEntity));
        when(jobMapping.jobToDto(jobEntity)).thenReturn(jobDto);

        Optional<JobDto> result = jobService.getJobById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getJobId());
    }

    @Test
    void getJobById_ShouldThrowNotFoundWhenMissing() {
        when(jobRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> jobService.getJobById(999));
    }

    @Test
    void updateJob_ShouldUpdateFields() {
        when(jobRepository.findById(1)).thenReturn(Optional.of(jobEntity));
        when(jobMapping.jobToDto(jobEntity)).thenReturn(jobDto);
        when(jobRepository.save(jobEntity)).thenReturn(jobEntity);

        JobDto incoming = new JobDto();
        incoming.setVehicleId(11);
        incoming.setEmployeeId(21);
        incoming.setOrderId(31);
        incoming.setIsComplete(true);

        VehicleEntity newVehicle = new VehicleEntity();
        newVehicle.setVehicleId(11);
        EmployeeEntity newEmployee = new EmployeeEntity(21, "Bob", 2, "otherPass");
        OrderEntity newOrder = new OrderEntity();
        newOrder.setOrderId(31);

        when(vehicleRepository.findById(11)).thenReturn(Optional.of(newVehicle));
        when(employeeRepository.findById(21)).thenReturn(Optional.of(newEmployee));
        when(orderRepository.findById(31)).thenReturn(Optional.of(newOrder));

        Optional<JobDto> result = jobService.updateJob(1, incoming);

        assertTrue(result.isPresent());
        assertTrue(result.get().getIsComplete());
        verify(jobRepository).save(jobEntity);
    }

    @Test
    void updateJob_ShouldThrowNotFoundWhenJobMissing() {
        when(jobRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> jobService.updateJob(1, jobDto));
        verify(jobRepository, never()).save(any());
    }

    @Test
    void updateJob_ShouldThrowNotFoundWhenVehicleMissing() {
        when(jobRepository.findById(1)).thenReturn(Optional.of(jobEntity));
        JobDto incoming = new JobDto();
        incoming.setVehicleId(999);
        when(vehicleRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> jobService.updateJob(1, incoming));
        verify(jobRepository, never()).save(any());
    }

    @Test
    void searchDoneJobsTable_ShouldReturnPageResponse() {
        DoneJobSearchCriteria criteria = new DoneJobSearchCriteria();
        criteria.setSortDirection("asc");
        criteria.setSortBy("vehicleId");

        Page<JobEntity> page = new PageImpl<>(List.of(jobEntity));
        Page<DoneJobTableInfoDto> mapped = new PageImpl<>(List.of(new DoneJobTableInfoDto()));

        when(jobRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(jobMapping.jobPageToDoneJobDtoPage(page, PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "vehicle.vehicleId"))))
                .thenReturn(mapped);

        PageResponse<DoneJobTableInfoDto> response = jobService.searchDoneJobsTable(criteria);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(jobRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchNotDoneJobsTable_ShouldReturnPageResponse() {
        NotDoneJobSearchCriteria criteria = new NotDoneJobSearchCriteria();
        criteria.setSortDirection("asc");
        criteria.setSortBy("registrationPlate");

        Page<JobEntity> page = new PageImpl<>(List.of(jobEntity));
        Page<NotDoneJobTableInfoDto> mapped = new PageImpl<>(List.of(new NotDoneJobTableInfoDto()));

        when(jobRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(jobMapping.jobPageToNotDoneJobDtoPage(page, PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "vehicle.registrationPlate"))))
                .thenReturn(mapped);

        PageResponse<NotDoneJobTableInfoDto> response = jobService.searchNotDoneJobsTable(criteria);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(jobRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}
