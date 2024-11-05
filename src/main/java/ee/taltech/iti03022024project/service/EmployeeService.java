package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.EmployeeDto;
import ee.taltech.iti03022024project.dto.LoginRequestDto;
import ee.taltech.iti03022024project.dto.LoginResponseDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.LoginFailedException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.EmployeeMapping;
import ee.taltech.iti03022024project.repository.EmployeeRepository;
import ee.taltech.iti03022024project.security.ApplicationConfiguration;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapping employeeMapping;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationConfiguration applicationConfiguration;

    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        if (employeeRepository.existsByNameIgnoreCase(employeeDto.getName())) {
            throw new AlreadyExistsException("Employee with name " + employeeDto.getName() + " already exists.");
        }
        String hashPassword = passwordEncoder.encode(employeeDto.getPassword());
        EmployeeEntity employeeEntity = new EmployeeEntity(null, employeeDto.getName(),
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

    public LoginResponseDto login(EmployeeDto employeeDto) {
        if (!employeeRepository.existsByName(employeeDto.getName()) ||
                !passwordEncoder.matches(employeeDto.getPassword(),
                        employeeRepository.getPasswordByName(employeeDto.getName()))) {
            throw new LoginFailedException("Username or password is incorrect!");

        }
        EmployeeEntity employeeEntity = employeeRepository.getByNameIgnoreCase(employeeDto.getName());
        String token = generateToken(employeeEntity);
        return new LoginResponseDto(token);
    }

    private String generateToken(EmployeeEntity employeeEntity) {
        return Jwts.builder()
                .subject(employeeEntity.getName())
                .claims(Map.of(
                        "employeeID", employeeEntity.getEmployeeId()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(applicationConfiguration.jwtkey())
                .compact();
    }

//    public String getPassword(LoginRequestDto requestDto) {
//        return employeeRepository.getPasswordByName(requestDto.getName());
//    }
}
