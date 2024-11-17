package ee.taltech.iti03022024project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name = "job")
public class JobEntity {

    public JobEntity(Integer jobId, VehicleEntity vehicle, EmployeeEntity employee, OrderEntity order, LocalDateTime pickupDate, LocalDateTime dropOffDate, Boolean isComplete) {
        this.jobId = jobId;
        this.vehicle = vehicle;
        this.employee = employee;
        this.order = order;
        this.pickupDate = pickupDate;
        this.dropOffDate = dropOffDate;
        this.isComplete = isComplete;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer jobId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    private LocalDateTime pickupDate;

    private LocalDateTime dropOffDate;

    private Boolean isComplete;

    @OneToOne(mappedBy = "job")
    private FuelConsumptionEntity fuelConsumption;
}
