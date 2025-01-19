package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.OrderDto;
import ee.taltech.iti03022024project.dto.OrderNameIdDto;
import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.query.OrdersTableInfoDto;
import ee.taltech.iti03022024project.dto.searchcriteria.OrderSearchCriteria;
import ee.taltech.iti03022024project.entity.CustomerEntity;
import ee.taltech.iti03022024project.entity.OrderEntity;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.OrderMapping;
import ee.taltech.iti03022024project.repository.CustomerRepository;
import ee.taltech.iti03022024project.repository.OrderRepository;
import ee.taltech.iti03022024project.repository.specifications.OrderSpecifications;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderMapping orderMapping;

    @InjectMocks
    private OrderService orderService;

    private OrderDto orderDto;
    private OrderEntity orderEntity;
    private CustomerEntity customerEntity;

    @BeforeEach
    void setUp() {
        orderDto = new OrderDto();
        orderDto.setOrderId(100);
        orderDto.setCustomerId(200);
        orderDto.setPickupDate(LocalDateTime.of(2025, 1, 1, 9, 0));
        orderDto.setDropOffDate(LocalDateTime.of(2025, 1, 10, 18, 0));
        orderDto.setWeight(12345);
        orderDto.setWidth(50);
        orderDto.setHeight(60);
        orderDto.setLength(70);
        orderDto.setOrderDetails("Some details");

        orderEntity = new OrderEntity();
        orderEntity.setOrderId(100);

        customerEntity = new CustomerEntity();
        customerEntity.setCustomerId(200);
        customerEntity.setName("Test Customer");
    }

    @Test
    void createOrder_ShouldCreateWhenCustomerExists() {
        when(customerRepository.findById(200)).thenReturn(Optional.of(customerEntity));
        when(orderMapping.orderToEntity(orderDto)).thenReturn(orderEntity);
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderMapping.orderToDto(orderEntity)).thenReturn(orderDto);

        OrderDto result = orderService.createOrder(orderDto);

        assertNotNull(result);
        assertEquals(100, result.getOrderId());
        verify(orderRepository).save(orderEntity);
    }

    @Test
    void createOrder_ShouldThrowNotFoundWhenCustomerMissing() {
        when(customerRepository.findById(200)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.createOrder(orderDto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getAllOrders_ShouldReturnList() {
        List<OrderEntity> orders = List.of(orderEntity);
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapping.orderListToDtoList(orders)).thenReturn(List.of(orderDto));

        List<OrderDto> result = orderService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getOrderId());
        verify(orderRepository).findAll();
    }

    @Test
    void getOrderById_ShouldReturnWhenFound() {
        when(orderRepository.findById(100)).thenReturn(Optional.of(orderEntity));
        when(orderMapping.orderToDto(orderEntity)).thenReturn(orderDto);

        Optional<OrderDto> result = orderService.getOrderById(100);

        assertTrue(result.isPresent());
        assertEquals(100, result.get().getOrderId());
    }

    @Test
    void getOrderById_ShouldThrowNotFoundWhenMissing() {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.getOrderById(999));
    }

    @Test
    void updateOrder_ShouldUpdateWhenFound() {
        when(orderRepository.findById(100)).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderMapping.orderToDto(orderEntity)).thenReturn(orderDto);

        OrderDto incoming = new OrderDto();
        incoming.setCustomerId(200);
        incoming.setOrderDetails("Updated Details");

        when(customerRepository.findById(200)).thenReturn(Optional.of(customerEntity));

        Optional<OrderDto> result = orderService.updateOrder(100, incoming);

        assertTrue(result.isPresent());
        verify(orderRepository).save(orderEntity);
        assertEquals("Updated Details", result.get().getOrderDetails());
    }

    @Test
    void updateOrder_ShouldThrowNotFoundWhenOrderMissing() {
        when(orderRepository.findById(100)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.updateOrder(100, orderDto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrder_ShouldThrowNotFoundWhenCustomerMissing() {
        when(orderRepository.findById(100)).thenReturn(Optional.of(orderEntity));
        OrderDto incoming = new OrderDto();
        incoming.setCustomerId(999);
        when(customerRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> orderService.updateOrder(100, incoming));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void searchOrdersTable_ShouldReturnPageResponse() {
        OrderSearchCriteria criteria = new OrderSearchCriteria();
        criteria.setCustomerName("Test");
        criteria.setSortDirection("asc");
        criteria.setSortBy("customerName");
        criteria.setPage(0);
        criteria.setSize(2);

        Page<OrderEntity> page = new PageImpl<>(List.of(orderEntity));
        Page<OrdersTableInfoDto> mappedPage = new PageImpl<>(List.of(new OrdersTableInfoDto(
                1,
                "Customer name",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                6000,
                200,
                450,
                1500,
                "some details"
        )));

        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(orderMapping.orderPageToDtoPage(page,
                PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "customer.name"))))
                .thenReturn(mappedPage);

        PageResponse<OrdersTableInfoDto> response = orderService.searchOrdersTable(criteria);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(orderRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getOrdersWithoutJob_ShouldReturnList() {
        List<OrderNameIdDto> dtos = List.of(new OrderNameIdDto());
        when(orderRepository.getOrdersWithoutJob()).thenReturn(dtos);

        List<OrderNameIdDto> result = orderService.getOrdersWithoutJob();

        assertEquals(1, result.size());
        assertEquals("Test Customer", result.getFirst().getCustomerName());
        verify(orderRepository).getOrdersWithoutJob();
    }
}
