package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.EmployeeDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
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
        EmployeeEntity employeeEntity = new EmployeeEntity(null, employeeDto.getName(), null);
        EmployeeEntity savedEmployeeEntity = employeeRepository.save(employeeEntity);
        return employeeMapping.employeeToDto(savedEmployeeEntity);
    }

    public List<EmployeeDto> getAllEmployees() {
        List<EmployeeEntity> employeeEntities = employeeRepository.findAll();
        return employeeMapping.employeeListToDtoList(employeeEntities);
    }

    public Optional<EmployeeDto> getEmployeeById(Integer id) {
        Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(id);
        return employeeEntity.map(employeeMapping::employeeToDto);
    }
}
