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
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeMapping employeeMapping;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ApplicationConfiguration applicationConfiguration;

    @InjectMocks
    private EmployeeService employeeService;

    private CreateEmployeeDto createEmployeeDto;
    private EmployeeEntity employeeEntity;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        createEmployeeDto = new CreateEmployeeDto();
        createEmployeeDto.setName("John");
        createEmployeeDto.setPermissionId(2);
        createEmployeeDto.setPassword("secret");

        employeeEntity = new EmployeeEntity(1, "John", 2, "encodedPassword");

        employeeDto = new EmployeeDto();
        employeeDto.setEmployeeId(1);
        employeeDto.setName("John");
        employeeDto.setPermissionId(2);
    }

    @Test
    void createEmployee_ShouldCreateWhenValid() {
        when(employeeRepository.existsByNameIgnoreCase("John")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encodedPassword");
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(employeeEntity);
        when(employeeMapping.employeeToDto(employeeEntity)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.createEmployee(createEmployeeDto);

        assertNotNull(result);
        assertEquals("John", result.getName());
        verify(employeeRepository).save(any(EmployeeEntity.class));
    }

    @Test
    void createEmployee_ShouldThrowWrongValueWhenNameIsNull() {
        createEmployeeDto.setName(null);
        assertThrows(WrongValueException.class, () -> employeeService.createEmployee(createEmployeeDto));
    }

    @Test
    void createEmployee_ShouldThrowWrongValueWhenPasswordIsEmpty() {
        createEmployeeDto.setPassword("");
        assertThrows(WrongValueException.class, () -> employeeService.createEmployee(createEmployeeDto));
    }

    @Test
    void createEmployee_ShouldThrowAlreadyExistsWhenNameConflict() {
        when(employeeRepository.existsByNameIgnoreCase("John")).thenReturn(true);
        assertThrows(AlreadyExistsException.class, () -> employeeService.createEmployee(createEmployeeDto));
    }

    @Test
    void createEmployee_ShouldThrowWrongValueWhenPermissionIdInvalid() {
        createEmployeeDto.setPermissionId(99);
        assertThrows(WrongValueException.class, () -> employeeService.createEmployee(createEmployeeDto));
    }

    @Test
    void getAllEmployees_ShouldReturnList() {
        when(employeeRepository.findAll()).thenReturn(List.of(employeeEntity));
        when(employeeMapping.employeeListToDtoList(List.of(employeeEntity))).thenReturn(List.of(employeeDto));

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void getAllEmployees_ShouldThrowNotFoundIfEmpty() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(NotFoundException.class, () -> employeeService.getAllEmployees());
    }

    @Test
    void getEmployeeById_ShouldReturnOptionalWhenFound() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employeeEntity));
        when(employeeMapping.employeeToDto(employeeEntity)).thenReturn(employeeDto);

        Optional<EmployeeDto> result = employeeService.getEmployeeById(1);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getName());
    }

    @Test
    void getEmployeeById_ShouldThrowNotFoundWhenMissing() {
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> employeeService.getEmployeeById(999));
    }

    @Test
    void updateEmployee_ShouldUpdateWhenValid() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employeeEntity));
        when(employeeMapping.employeeToDto(employeeEntity)).thenReturn(employeeDto);
        when(passwordEncoder.encode("newPass")).thenReturn("hashedNewPass");
        when(employeeRepository.save(employeeEntity)).thenReturn(employeeEntity);

        EmployeeDto updatedDto = new EmployeeDto();
        updatedDto.setEmployeeId(1);
        updatedDto.setName("Mike");
        updatedDto.setPermissionId(3);

        when(employeeMapping.employeeToDto(employeeEntity)).thenReturn(updatedDto);

        Optional<EmployeeDto> result = employeeService.updateEmployee(1, "Mike", 3, "newPass");

        assertTrue(result.isPresent());
        assertEquals("Mike", result.get().getName());
        verify(employeeRepository).save(employeeEntity);
    }

    @Test
    void updateEmployee_ShouldThrowNotFoundWhenMissing() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> employeeService.updateEmployee(1, "X", 1, "Pass"));
    }

    @Test
    void updateEmployee_ShouldThrowAlreadyExistsWhenNameDuplicated() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employeeEntity));
        when(employeeMapping.employeeToDto(employeeEntity)).thenReturn(employeeDto);
        when(employeeRepository.existsByNameIgnoreCase("Mike")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> employeeService.updateEmployee(1, "Mike", 2, "xxx"));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_ShouldThrowWrongValueWhenPermissionIdInvalid() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employeeEntity));
        when(employeeMapping.employeeToDto(employeeEntity)).thenReturn(employeeDto);

        assertThrows(WrongValueException.class, () -> employeeService.updateEmployee(1, "NewName", 99, "NewPass"));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void searchEmployeeTable_ShouldReturnPageResponse() {
        EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
        criteria.setEmployeeName("John");
        criteria.setPage(0);
        criteria.setSize(10);
        criteria.setSortBy("employeeName");
        criteria.setSortDirection("asc");

        Page<EmployeeEntity> page = new PageImpl<>(List.of(employeeEntity));
        EmployeeTableInfoDto infoDto = new EmployeeTableInfoDto(
                1, "John", "Permission", "['B', 'CE', 'D']", null
        );
        Page<EmployeeTableInfoDto> mappedPage = new PageImpl<>(List.of(infoDto));

        when(employeeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(employeeMapping.employeePageToTableInfoDtoPage(page, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))))
                .thenReturn(mappedPage);

        PageResponse<EmployeeTableInfoDto> response = employeeService.searchEmployeeTable(criteria);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(employeeRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void login_ShouldReturnTokenWhenValid() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setName("John");
        loginRequest.setPassword("secret");

        when(employeeRepository.getByNameIgnoreCase("John")).thenReturn(Optional.of(employeeEntity));
        when(passwordEncoder.matches("secret", "encodedPassword")).thenReturn(true);

        Key key = Keys.hmacShaKeyFor("01234567890123456789012345678901".getBytes());
        when(applicationConfiguration.jwtkey()).thenReturn((SecretKey) key);

        LoginResponseDto response = employeeService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
    }

    @Test
    void login_ShouldThrowLoginFailedWhenUserMissing() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setName("Missing");
        loginRequest.setPassword("secret");

        when(employeeRepository.getByNameIgnoreCase("Missing")).thenReturn(Optional.empty());
        assertThrows(LoginFailedException.class, () -> employeeService.login(loginRequest));
    }

    @Test
    void login_ShouldThrowLoginFailedWhenPasswordMismatch() {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setName("John");
        loginRequest.setPassword("wrong");

        when(employeeRepository.getByNameIgnoreCase("John")).thenReturn(Optional.of(employeeEntity));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        assertThrows(LoginFailedException.class, () -> employeeService.login(loginRequest));
    }
}
