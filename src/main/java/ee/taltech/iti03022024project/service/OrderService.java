package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.OrderDto;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapping orderMapping;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderDto createOrder(OrderDto orderDto) {
        log.info("Creating order with details: {}", orderDto);

        CustomerEntity customer = customerRepository.findById(orderDto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer with ID " + orderDto.getCustomerId() + " does not exist."));

        OrderEntity orderEntity = orderMapping.orderToEntity(orderDto);
        orderEntity.setCustomer(customer);

        OrderEntity savedOrder = orderRepository.save(orderEntity);
        log.info("Order with ID {} created successfully.", savedOrder.getOrderId());
        return orderMapping.orderToDto(savedOrder);
    }

    public List<OrderDto> getAllOrders() {
        log.info("Fetching all orders.");
        List<OrderEntity> orders = orderRepository.findAll();
        return orderMapping.orderListToDtoList(orders);
    }

    public Optional<OrderDto> getOrderById(Integer id) {
        log.info("Fetching order with ID: {}", id);
        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with ID " + id + " does not exist."));
        return Optional.of(orderMapping.orderToDto(orderEntity));
    }

    public Optional<OrderDto> updateOrder(Integer id, OrderDto orderDto) {
        log.info("Updating order with ID: {}", id);

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
        log.info("Order with ID {} updated successfully.", updatedOrder.getOrderId());
        return Optional.of(orderMapping.orderToDto(updatedOrder));
    }

    public PageResponse<OrdersTableInfoDto> searchOrdersTable(OrderSearchCriteria criteria) {
        log.info("Searching orders with criteria: {}", criteria);

        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "orderId";
        String direction = criteria.getSortDirection() != null ? criteria.getSortDirection().toUpperCase() : "DESC";
        int pageNumber = criteria.getPage() != null ? criteria.getPage() : 0;
        int pageSize = criteria.getSize() != null ? criteria.getSize() : 20;

        Sort sort = Sort.by(Sort.Direction.valueOf(direction), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Specification<OrderEntity> spec = Specification
                .where(OrderSpecifications.orderId(criteria.getOrderId()))
                .and(OrderSpecifications.customerNameLike(criteria.getCustomerName()))
                .and(OrderSpecifications.pickupDateBetween(criteria.getPickupStartDate(), criteria.getPickupEndDate()))
                .and(OrderSpecifications.dropOffDateBetween(criteria.getDropOffStartDate(), criteria.getDropOffEndDate()))
                .and(OrderSpecifications.weightBetween(criteria.getMinWeight(), criteria.getMaxWeight()))
                .and(OrderSpecifications.lengthBetween(criteria.getMinLength(), criteria.getMaxLength()))
                .and(OrderSpecifications.widthBetween(criteria.getMinWidth(), criteria.getMaxWidth()))
                .and(OrderSpecifications.heightBetween(criteria.getMinHeight(), criteria.getMaxHeight()));

        Page<OrderEntity> orderPage = orderRepository.findAll(spec, pageable);

        Page<OrdersTableInfoDto> dtoPage = orderPage.map(order -> new OrdersTableInfoDto(
                order.getOrderId(),
                order.getCustomer().getName(),
                order.getPickupDate(),
                order.getDropOffDate(),
                order.getWeight(),
                order.getWidth(),
                order.getHeight(),
                order.getLength(),
                order.getOrderDetails()
        ));

        log.info("Found {} orders matching the criteria.", dtoPage.getTotalElements());
        return new PageResponse<>(dtoPage);
    }
}
