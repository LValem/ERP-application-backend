package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.employee.CreateEmployeeDto;
import ee.taltech.iti03022024project.dto.employee.EmployeeDto;
import ee.taltech.iti03022024project.dto.employee.LoginRequestDto;
import ee.taltech.iti03022024project.dto.employee.LoginResponseDto;
import ee.taltech.iti03022024project.dto.query.EmployeeTableInfoDto;
import ee.taltech.iti03022024project.dto.searchcriteria.EmployeeSearchCriteria;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.exception.*;
import ee.taltech.iti03022024project.mapping.EmployeeMapping;
import ee.taltech.iti03022024project.repository.EmployeeRepository;
import ee.taltech.iti03022024project.repository.specifications.EmployeeSpecifications;
import ee.taltech.iti03022024project.security.ApplicationConfiguration;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.temporal.ValueRange;
import java.util.*;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapping employeeMapping;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationConfiguration applicationConfiguration;

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    public EmployeeDto createEmployee(CreateEmployeeDto createEmployeeDto) {
        log.info("Attempting to create employee with name: {} and permissionID: {}", createEmployeeDto.getName(), createEmployeeDto.getPermissionId());

        if (createEmployeeDto.getName() == null || createEmployeeDto.getName().isEmpty()
                || createEmployeeDto.getPassword() == null || createEmployeeDto.getPassword().isEmpty()) {
            throw new WrongValueException("Name or password can't be null!");
        }

        if (employeeRepository.existsByNameIgnoreCase(createEmployeeDto.getName())) {
            throw new AlreadyExistsException("Employee with name " + createEmployeeDto.getName() + " already exists.");
        }

        Integer permissionId = createEmployeeDto.getPermissionId();
        if (permissionId == null || (permissionId < 1 || permissionId > 3)) {
            throw new WrongValueException("Permission ID must be between 1 and 3");
        }

        String hashPassword = passwordEncoder.encode(createEmployeeDto.getPassword());
        EmployeeEntity employeeEntity = new EmployeeEntity(null, createEmployeeDto.getName(),
                permissionId, hashPassword);
        EmployeeEntity savedEmployeeEntity = employeeRepository.save(employeeEntity);

        log.info("Employee with name: {} created successfully!", employeeEntity.getName());
        return employeeMapping.employeeToDto(savedEmployeeEntity);
    }

    public List<EmployeeDto> getAllEmployees() {
        log.info("Fetching all employees.");

        List<EmployeeEntity> employeeEntities = employeeRepository.findAll();
        if (employeeEntities.isEmpty()) {
            throw new NotFoundException("There are no employees!");
        }

        log.info("Successfully fetched all employees!");
        return employeeMapping.employeeListToDtoList(employeeEntities);
    }

    public Optional<EmployeeDto> getEmployeeById(Integer id) {
        log.info("Fetching employee with ID: {}", id);

        Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(id);
        // Throw exception if not found
        if (employeeEntity.isEmpty()) {
            throw new NotFoundException("Employee with ID: " + id + " does not exist");
        }

        log.info("Successfully fetched employee with ID: {}", id);
        return employeeEntity.map(employeeMapping::employeeToDto);
    }

    public Optional<EmployeeDto> updateEmployee(Integer id, String name, Integer permissionId, String password) {
        log.info("Attempting to update employee with ID: {}", id);

        // Find the employee by ID
        Optional<EmployeeEntity> employeeEntityOpt = employeeRepository.findById(id);

        // Throw an exception if the employee is not found
        EmployeeEntity employeeEntity = employeeEntityOpt
                .orElseThrow(() -> new NotFoundException("Employee with this ID does not exist"));

        // Update fields if the new values are provided (not null)
        Optional<EmployeeDto> employeeOpt = getEmployeeById(id);

        if (name != null && !name.isEmpty() && employeeOpt.isPresent() && !Objects.equals(employeeOpt.get().getName(), name)) {
            if (!employeeRepository.existsByNameIgnoreCase(name)) {
                employeeEntity.setName(name);
            } else {
                throw new AlreadyExistsException("Cannot change name to " + name + " ,because " + name + " already exists!");
            }
        }

        ValueRange range = ValueRange.of(1, 3);
        if (permissionId != null) {
            if (range.isValidIntValue(permissionId)) {
                employeeEntity.setPermissionId(permissionId);
            } else {
                throw new WrongValueException(permissionId + " is not a real PermissionID!");
            }
        }

        if (password != null && !password.trim().isEmpty()) {
            String hashPassword = passwordEncoder.encode(password);
            employeeEntity.setPassword(hashPassword);
        }

        // Save the updated employee entity
        EmployeeEntity updatedEmployee = employeeRepository.save(employeeEntity);

        log.info("Employee with ID: {} updated successfully!", id);
        return Optional.of(employeeMapping.employeeToDto(updatedEmployee));
    }

    public PageResponse<EmployeeTableInfoDto> searchEmployeeTable(EmployeeSearchCriteria criteria) {
        log.info("Searching employees with criteria: {}", criteria);

        int page = criteria.getPage() != null ? criteria.getPage() : 0;
        int size = criteria.getSize() != null ? criteria.getSize() : 20;
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "employeeId";
        if ("employeeName".equals(sortBy)) {
            sortBy = "name";
        }
        Sort.Direction direction = (criteria.getSortDirection() == null || "desc".equalsIgnoreCase(criteria.getSortDirection()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Specification<EmployeeEntity> spec = Specification.where(
                EmployeeSpecifications.employeeId(criteria.getEmployeeId())
                        .and(EmployeeSpecifications.employeeNameLike(criteria.getEmployeeName()))
                        .and(EmployeeSpecifications.permissionDescription(criteria.getPermissionDescription()))
                        .and(EmployeeSpecifications.certificationNamesLike(criteria.getCertificationNames()))
                        .and(EmployeeSpecifications.lastJobDateBetween(criteria.getLastJobStartDate(), criteria.getLastJobEndDate()))
        );

        Pageable pageable;
        switch (sortBy) {
            case "lastJobDate" -> {
                spec = spec.and(EmployeeSpecifications.sortByLastJobDate(direction));
                pageable = PageRequest.of(page, size);
            }
            case "permissionDescription" -> {
                spec = spec.and(EmployeeSpecifications.sortByPermissionDescription(direction));
                pageable = PageRequest.of(page, size);
            }
            case "certificationNames" -> {
                spec = spec.and(EmployeeSpecifications.sortByCertificationNames(direction));
                pageable = PageRequest.of(page, size);
            }
            default -> pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        }

        Page<EmployeeEntity> employeeEntities = employeeRepository.findAll(spec, pageable);
        Page<EmployeeTableInfoDto> employeeDtos = employeeMapping.employeePageToTableInfoDtoPage(employeeEntities, pageable);
        log.info("Fetched {} employees based on search criteria.", employeeDtos.getTotalElements());
        return new PageResponse<>(employeeDtos);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("Attempting to log in employee with name: {}", loginRequestDto.getName());

        Optional<EmployeeEntity> user = employeeRepository.getByNameIgnoreCase(loginRequestDto.getName());

        if (user.isEmpty() || !passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())) {
            throw new LoginFailedException("Username or password is incorrect!");
        }
        String token = generateToken(user.get());

        log.info("Employee with name: {} logged in successfully.", loginRequestDto.getName());
        return new LoginResponseDto(token);
    }

    private String generateToken(EmployeeEntity employeeEntity) {
        return Jwts.builder()
                .subject(employeeEntity.getName())
                .claims(Map.of(
                        "employeeId", employeeEntity.getEmployeeId(),
                        "name", employeeEntity.getName(),
                        "permissionId", employeeEntity.getPermissionId()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(applicationConfiguration.jwtkey())
                .compact();
    }
}
