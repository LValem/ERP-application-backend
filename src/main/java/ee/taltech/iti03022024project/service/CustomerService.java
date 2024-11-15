package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.CustomerDto;
import ee.taltech.iti03022024project.entity.CustomerEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.CustomerMapping;
import ee.taltech.iti03022024project.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapping customerMapping;

    public CustomerDto createCustomer(CustomerDto customerDto) {
        if (customerRepository.existsByNameIgnoreCase(customerDto.getName())) {
            throw new AlreadyExistsException("Employee with name " + customerDto.getName() + " already exists.");
        }
        CustomerEntity customerEntity = customerMapping.customerToEntity(customerDto);
        CustomerEntity savedCustomer = customerRepository.save(customerEntity);
        return customerMapping.customerToDto(savedCustomer);
    }

    public List<CustomerDto> getAllCustomers() {
        List<CustomerEntity> customers = customerRepository.findAll();
        return customerMapping.customerListToDtoList(customers);
    }

    public Optional<CustomerDto> getCustomerById(Integer id) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer with ID " + id + " does not exist."));
        return Optional.of(customerMapping.customerToDto(customerEntity));
    }

    public Optional<CustomerDto> updateCustomer(CustomerDto customerDto) {
        CustomerEntity customerEntity = customerRepository.findById(customerDto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer with ID " + customerDto.getCustomerId() + " does not exist."));

        if (customerDto.getName() != null) {
            if (!customerRepository.existsByNameIgnoreCase(customerDto.getName())) {
                customerEntity.setName(customerDto.getName());
            } else {
                throw new AlreadyExistsException("Customer with name " + customerDto.getName() + " already exists.");
            }
        }
        if (customerDto.getAddress() != null) {
            customerEntity.setAddress(customerDto.getAddress());
        }
        if (customerDto.getCityCounty() != null) {
            customerEntity.setCityCounty(customerDto.getCityCounty());
        }
        if (customerDto.getZip() != null) {
            customerEntity.setZip(customerDto.getZip());
        }
        if (customerDto.getEmail() != null) {
            customerEntity.setEmail(customerDto.getEmail());
        }
        if (customerDto.getPhoneNumber() != null) {
            customerEntity.setPhoneNumber(customerDto.getPhoneNumber());
        }
        if (customerDto.getVatNo() != null) {
            customerEntity.setVatNo(customerDto.getVatNo());
        }

        CustomerEntity updatedCustomer = customerRepository.save(customerEntity);
        return Optional.of(customerMapping.customerToDto(updatedCustomer));
    }
}
