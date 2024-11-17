package ee.taltech.iti03022024project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fuel_consumption")
public class FuelConsumptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fuelConsumptionId;

    @OneToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobEntity job;

    @OneToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleEntity vehicle;

    private Double fuelUsed;
    private Double distanceDriven;
    private LocalDateTime consumptionDate;
}
