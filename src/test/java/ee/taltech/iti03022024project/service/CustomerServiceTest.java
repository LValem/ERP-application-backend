package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.CustomerDto;
import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.query.CustomerTableInfoDto;
import ee.taltech.iti03022024project.dto.searchcriteria.CustomerSearchCriteria;
import ee.taltech.iti03022024project.entity.CustomerEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.CustomerMapping;
import ee.taltech.iti03022024project.repository.CustomerRepository;
import ee.taltech.iti03022024project.repository.specifications.CustomerSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapping customerMapping;

    @InjectMocks
    private CustomerService customerService;

    private CustomerDto customerDto;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        customerDto = new CustomerDto();
        customerDto.setCustomerId(1);
        customerDto.setName("ABC Corp");
        customerDto.setAddress("123 Street");
        customerDto.setCityCounty("City");
        customerDto.setZip("12345");
        customerDto.setEmail("info@abc.com");
        customerDto.setPhoneNumber("555-1234");
        customerDto.setVatNo("VAT123");

        customerEntity = new CustomerEntity();
        customerEntity.setCustomerId(1);
        customerEntity.setName("ABC Corp");
        customerEntity.setAddress("123 Street");
        customerEntity.setCityCounty("City");
        customerEntity.setZip("12345");
        customerEntity.setEmail("info@abc.com");
        customerEntity.setPhoneNumber("555-1234");
        customerEntity.setVatNo("VAT123");
    }

    @Test
    void createCustomer_ShouldCreateWhenNameUnique() {
        when(customerRepository.existsByNameIgnoreCase("ABC Corp")).thenReturn(false);
        when(customerMapping.customerToEntity(customerDto)).thenReturn(customerEntity);
        when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapping.customerToDto(customerEntity)).thenReturn(customerDto);

        CustomerDto result = customerService.createCustomer(customerDto);

        assertNotNull(result);
        assertEquals("ABC Corp", result.getName());
        verify(customerRepository).save(customerEntity);
    }

    @Test
    void createCustomer_ShouldThrowAlreadyExistsWhenNameDuplicate() {
        when(customerRepository.existsByNameIgnoreCase("ABC Corp")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> customerService.createCustomer(customerDto));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void getAllCustomers_ShouldReturnList() {
        List<CustomerEntity> entities = List.of(customerEntity);
        List<CustomerDto> dtos = List.of(customerDto);

        when(customerRepository.findAll()).thenReturn(entities);
        when(customerMapping.customerListToDtoList(entities)).thenReturn(dtos);

        List<CustomerDto> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("ABC Corp", result.getFirst().getName());
        verify(customerRepository).findAll();
    }

    @Test
    void getCustomerById_ShouldReturnWhenFound() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customerEntity));
        when(customerMapping.customerToDto(customerEntity)).thenReturn(customerDto);

        Optional<CustomerDto> result = customerService.getCustomerById(1);

        assertTrue(result.isPresent());
        assertEquals("ABC Corp", result.get().getName());
    }

    @Test
    void getCustomerById_ShouldThrowNotFoundWhenMissing() {
        when(customerRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.getCustomerById(999));
    }

    @Test
    void updateCustomer_ShouldUpdateFields() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customerEntity));
        when(customerService.getCustomerById(1)).thenReturn(Optional.of(customerDto));
        when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapping.customerToDto(customerEntity)).thenReturn(customerDto);

        CustomerDto updatedDto = new CustomerDto();
        updatedDto.setCustomerId(1);
        updatedDto.setName("New Name");
        updatedDto.setAddress("New Address");
        updatedDto.setCityCounty("New City");
        updatedDto.setZip("99999");
        updatedDto.setEmail("new@abc.com");
        updatedDto.setPhoneNumber("999-9999");
        updatedDto.setVatNo("NEWVAT");

        when(customerRepository.existsByNameIgnoreCase("New Name")).thenReturn(false);

        Optional<CustomerDto> result = customerService.updateCustomer(updatedDto);

        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getName());
        verify(customerRepository).save(customerEntity);
    }

    @Test
    void updateCustomer_ShouldThrowAlreadyExistsWhenNameExists() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customerEntity));
        when(customerService.getCustomerById(1)).thenReturn(Optional.of(customerDto));
        when(customerRepository.existsByNameIgnoreCase("Duplicate")).thenReturn(true);

        CustomerDto incoming = new CustomerDto();
        incoming.setCustomerId(1);
        incoming.setName("Duplicate");

        assertThrows(AlreadyExistsException.class, () -> customerService.updateCustomer(incoming));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_ShouldThrowNotFoundWhenMissing() {
        when(customerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> customerService.updateCustomer(customerDto));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void searchCustomerTable_ShouldReturnPageResponse() {
        CustomerSearchCriteria criteria = new CustomerSearchCriteria();
        criteria.setCustomerName("ABC");
        criteria.setPage(0);
        criteria.setSize(10);
        criteria.setSortBy("customerName");
        criteria.setSortDirection("asc");

        CustomerTableInfoDto infoDto = new CustomerTableInfoDto(
                1,
                "ABC Corp",
                "123 Street",
                "City",
                "12345",
                "info@abc.com",
                "555-1234",
                "VAT123",
                LocalDateTime.now()
        );

        Page<CustomerEntity> page = new PageImpl<>(List.of(customerEntity));
        Page<CustomerTableInfoDto> mappedPage = new PageImpl<>(List.of(infoDto));

        when(customerRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(customerMapping.customerPageToDtoPage(page, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"))))
                .thenReturn(mappedPage);

        PageResponse<CustomerTableInfoDto> response = customerService.searchCustomerTable(criteria);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(customerRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}
