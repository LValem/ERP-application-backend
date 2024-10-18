package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.EmployeeDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.EmployeeMapping;
import ee.taltech.iti03022024project.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapping employeeMapping;

    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        if (employeeRepository.existsByNameIgnoreCase(employeeDto.getName())) {
            throw new AlreadyExistsException("Employee with name " + employeeDto.getName() + " already exists.");
        }
        EmployeeEntity employeeEntity = new EmployeeEntity(null, employeeDto.getName(), 2);
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
}
