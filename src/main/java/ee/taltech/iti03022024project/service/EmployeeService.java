package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.employee.CreateEmployeeDto;
import ee.taltech.iti03022024project.dto.employee.EmployeeDto;
import ee.taltech.iti03022024project.dto.employee.LoginRequestDto;
import ee.taltech.iti03022024project.dto.employee.LoginResponseDto;
import ee.taltech.iti03022024project.dto.query.EmployeeTableInfoDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.exception.*;
import ee.taltech.iti03022024project.mapping.EmployeeMapping;
import ee.taltech.iti03022024project.repository.EmployeeRepository;
import ee.taltech.iti03022024project.security.ApplicationConfiguration;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.temporal.ValueRange;
import java.util.*;
import java.util.stream.Collectors;

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
        employeeEntity.orElseThrow(() -> new NotFoundException("Employee with this ID does not exist"));

        log.info("Successfully fetched employee with ID: {}", id);
        return employeeEntity.map(employeeMapping::employeeToDto);
    }

    public Optional<EmployeeDto> updateEmployee(Integer id, String name, Integer permissionId, String password) {
        System.out.println(name);
        System.out.println(permissionId);
        System.out.println(password);
        log.info("Attempting to update employee with ID: {}", id);

        // Find the employee by ID
        Optional<EmployeeEntity> employeeEntityOpt = employeeRepository.findById(id);

        // Throw an exception if the employee is not found
        EmployeeEntity employeeEntity = employeeEntityOpt
                .orElseThrow(() -> new NotFoundException("Employee with this ID does not exist"));

        // Update fields if the new values are provided (not null)
        if (name != null) {
            if (!Objects.equals(getEmployeeById(id).get().getName(), name)) {
                if (!name.isEmpty() && !employeeRepository.existsByNameIgnoreCase(name)) {
                    employeeEntity.setName(name);
                } else {
                    throw new AlreadyExistsException("Cannot change name to " + name + " ,because " + name + " already exists!");
                }
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

    public List<EmployeeTableInfoDto> getEmployeeTableInfo() {
        log.info("Fetching employee table information.");
        List<EmployeeTableInfoDto> result = aggregateEmployeeCertifications(employeeRepository.getEmployeeTableInfo());
        log.info("Successfully fetched employee table information with {} records.", result.size());
        return result;
    }

    private List<EmployeeTableInfoDto> aggregateEmployeeCertifications(List<EmployeeTableInfoDto> rawResults) {
        log.info("Aggregating employee certifications.");
        Map<String, EmployeeTableInfoDto> aggregatedResults = new HashMap<>();

        for (EmployeeTableInfoDto dto : rawResults) {
            String key = dto.getEmployeeName() + "-" + dto.getPermissionDescription();
            EmployeeTableInfoDto aggregatedDto = aggregatedResults.computeIfAbsent(key, k ->
                    new EmployeeTableInfoDto(
                            dto.getEmployeeId(),
                            dto.getEmployeeName(),
                            dto.getPermissionDescription(),
                            "",
                            dto.getLastJobDate()
                    )
            );
            if (dto.getCertificationNames() != null) {
                Set<String> certifications = new HashSet<>(Arrays.asList(aggregatedDto.getCertificationNames().split(", ")));
                certifications.add(dto.getCertificationNames());
                aggregatedDto.setCertificationNames(
                        certifications.stream().filter(s -> !s.isEmpty()).collect(Collectors.joining(", "))
                );
            }
            if (dto.getLastJobDate() != null &&
                    (aggregatedDto.getLastJobDate() == null || dto.getLastJobDate().isAfter(aggregatedDto.getLastJobDate()))) {
                aggregatedDto.setLastJobDate(dto.getLastJobDate());
            }
        }
        log.info("Employee certifications aggregated successfully.");
        return new ArrayList<>(aggregatedResults.values());
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
