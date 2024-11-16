package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.CreateEmployeeDto;
import ee.taltech.iti03022024project.dto.EmployeeDto;
import ee.taltech.iti03022024project.dto.LoginRequestDto;
import ee.taltech.iti03022024project.dto.LoginResponseDto;
import ee.taltech.iti03022024project.dto.query.EmployeeTableInfoDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.LoginFailedException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.EmployeeMapping;
import ee.taltech.iti03022024project.repository.EmployeeRepository;
import ee.taltech.iti03022024project.security.ApplicationConfiguration;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
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

    public EmployeeDto createEmployee(CreateEmployeeDto createEmployeeDto) {
        if (employeeRepository.existsByNameIgnoreCase(createEmployeeDto.getName())) {
            throw new AlreadyExistsException("Employee with name " + createEmployeeDto.getName() + " already exists.");
        }
        String hashPassword = passwordEncoder.encode(createEmployeeDto.getPassword());
        EmployeeEntity employeeEntity = new EmployeeEntity(null, createEmployeeDto.getName(),
                2, hashPassword);
        EmployeeEntity savedEmployeeEntity = employeeRepository.save(employeeEntity);
        return employeeMapping.employeeToDto(savedEmployeeEntity);
    }

    public List<EmployeeDto> getAllEmployees() {
        List<EmployeeEntity> employeeEntities = employeeRepository.findAll();
        if (employeeEntities.isEmpty()) {
            throw new NotFoundException("There are no employees!");
        }
        return employeeMapping.employeeListToDtoList(employeeEntities);
    }

    public Optional<EmployeeDto> getEmployeeById(Integer id) {
        Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(id);
        employeeEntity.orElseThrow(() -> new NotFoundException("Employee with this ID does not exist"));
        return employeeEntity.map(employeeMapping::employeeToDto);
    }

//    public Optional<EmployeeDto> updateEmployee(Integer id, String name, Integer permissionID, String password) {
//        Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(id);
//        employeeEntity.orElseThrow(() -> new NotFoundException("Employee with this ID does not exist"));
//
//        if ()
//    }

    public Optional<EmployeeDto> updateEmployee(Integer id, String name, Integer permissionID, String password) {

        System.out.println(id);
        System.out.println(name);
        System.out.println(permissionID);
        System.out.println(password);
        // Find the employee by ID
        Optional<EmployeeEntity> employeeEntityOpt = employeeRepository.findById(id);

        // Throw an exception if the employee is not found
        EmployeeEntity employeeEntity = employeeEntityOpt
                .orElseThrow(() -> new NotFoundException("Employee with this ID does not exist"));

        // Update fields if the new values are provided (not null)
        if (!name.isEmpty() && !employeeRepository.existsByNameIgnoreCase(name)) {
            employeeEntity.setName(name);
        } else {
            throw new AlreadyExistsException("Cannot change name to " + name + " ,because " + name + " already exists!");
        }

        ValueRange range = ValueRange.of(1, 3);
        if (permissionID != null) {
            if (range.isValidIntValue(permissionID)) {
                employeeEntity.setPermissionId(permissionID);
            } else {
                throw new IllegalArgumentException(permissionID + " is not a real PermissionID!");
            }
        }

        if (password != null && !password.trim().isEmpty()) {
            String hashPassword = passwordEncoder.encode(password);
            employeeEntity.setPassword(hashPassword);
        }

        // Save the updated employee entity
        EmployeeEntity updatedEmployee = employeeRepository.save(employeeEntity);

        // Convert the entity to a DTO and return it
        return Optional.of(employeeMapping.employeeToDto(updatedEmployee));
    }

    public List<EmployeeTableInfoDto> getEmployeeTableInfo() {
        return aggregateEmployeeCertifications(employeeRepository.getEmployeeTableInfo());
    }

    private List<EmployeeTableInfoDto> aggregateEmployeeCertifications(List<EmployeeTableInfoDto> rawResults) {
        Map<String, EmployeeTableInfoDto> aggregatedResults = new HashMap<>();

        for (EmployeeTableInfoDto dto : rawResults) {
            String key = dto.getEmployeeName() + "-" + dto.getPermissionDescription();
            EmployeeTableInfoDto aggregatedDto = aggregatedResults.computeIfAbsent(key, k ->
                    new EmployeeTableInfoDto(
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
        return new ArrayList<>(aggregatedResults.values());
    }


    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Optional<EmployeeEntity> user = employeeRepository.getByNameIgnoreCase(loginRequestDto.getName());

        if (user.isEmpty() || !passwordEncoder.matches(loginRequestDto.getPassword(), user.get().getPassword())) {
            throw new LoginFailedException("Username or password is incorrect!");
        }
        String token = generateToken(user.get());
        return new LoginResponseDto(token);
    }

//    private String generateToken(EmployeeEntity employeeEntity) {
//        return Jwts.builder()
//                .subject(employeeEntity.getName())
//                .claims(Map.of(
//                        "employeeID", employeeEntity.getEmployeeId()
//
//                ))
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
//                .signWith(applicationConfiguration.jwtkey())
//                .compact();
//    }

    private String generateToken(EmployeeEntity employeeEntity) {
        return Jwts.builder()
                .subject(employeeEntity.getName())
                .claims(Map.of(
                        "employeeID", employeeEntity.getEmployeeId(),
                        "name", employeeEntity.getName(),
                        "permissionID", employeeEntity.getPermissionId()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(applicationConfiguration.jwtkey())
                .compact();
    }

}
