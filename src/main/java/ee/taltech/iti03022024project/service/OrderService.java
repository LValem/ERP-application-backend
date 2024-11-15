package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.OrderDto;
import ee.taltech.iti03022024project.entity.CustomerEntity;
import ee.taltech.iti03022024project.entity.OrderEntity;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.OrderMapping;
import ee.taltech.iti03022024project.repository.CustomerRepository;
import ee.taltech.iti03022024project.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapping orderMapping;

    public OrderDto createOrder(OrderDto orderDto) {
        CustomerEntity customer = customerRepository.findById(orderDto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer with ID " + orderDto.getCustomerId() + " does not exist."));

        OrderEntity orderEntity = orderMapping.orderToEntity(orderDto);
        orderEntity.setCustomer(customer);

        OrderEntity savedOrder = orderRepository.save(orderEntity);
        return orderMapping.orderToDto(savedOrder);
    }

    public List<OrderDto> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        return orderMapping.orderListToDtoList(orders);
    }

    public Optional<OrderDto> getOrderById(Integer id) {
        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with ID " + id + " does not exist."));
        return Optional.of(orderMapping.orderToDto(orderEntity));
    }

    public Optional<OrderDto> updateOrder(Integer id, OrderDto orderDto) {
        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with ID " + id + " does not exist."));

        if (orderDto.getCustomerId() != null) {
            CustomerEntity customer = customerRepository.findById(orderDto.getCustomerId())
                    .orElseThrow(() -> new NotFoundException("Customer with ID " + orderDto.getCustomerId() + " does not exist."));
            orderEntity.setCustomer(customer);
        }
        if (orderDto.getPickupDate() != null) orderEntity.setPickupDate(orderDto.getPickupDate());
        if (orderDto.getDropOffDate() != null) orderEntity.setDropOffDate(orderDto.getDropOffDate());
        if (orderDto.getWeight() != null) orderEntity.setWeight(orderDto.getWeight());
        if (orderDto.getWidth() != null) orderEntity.setWidth(orderDto.getWidth());
        if (orderDto.getHeight() != null) orderEntity.setHeight(orderDto.getHeight());
        if (orderDto.getLength() != null) orderEntity.setLength(orderDto.getLength());
        if (orderDto.getOrderDetails() != null) orderEntity.setOrderDetails(orderDto.getOrderDetails());

        OrderEntity updatedOrder = orderRepository.save(orderEntity);
        return Optional.of(orderMapping.orderToDto(updatedOrder));
    }
}
