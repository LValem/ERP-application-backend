package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.dto.query.DoneJobTableInfoDto;
import ee.taltech.iti03022024project.dto.query.NotDoneJobTableInfoDto;
import ee.taltech.iti03022024project.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Integer>, JpaSpecificationExecutor<JobEntity> {

    @Query("""
    SELECT new ee.taltech.iti03022024project.dto.query.NotDoneJobTableInfoDto(
        j.jobId,
        j.vehicle.vehicleId,
        v.registrationPlate,
        j.order.orderId,
        c.name,
        o.pickupDate,
        o.dropOffDate,
        j.isComplete
    )
    FROM JobEntity j
    LEFT JOIN j.vehicle v
    LEFT JOIN j.order o
    LEFT JOIN o.customer c
    WHERE j.isComplete = FALSE
""")
    Page<NotDoneJobTableInfoDto> findAllNotDoneJobTableInfo(Specification<JobEntity> spec, Pageable pageable);

    @Query("""
    SELECT new ee.taltech.iti03022024project.dto.query.DoneJobTableInfoDto(
        j.jobId,
        j.vehicle.vehicleId,
        j.vehicle.registrationPlate,
        fc.fuelUsed,
        fc.distanceDriven,
        j.order.orderId,
        j.order.customer.name,
        j.pickupDate,
        j.dropOffDate,
        j.isComplete
    )
    FROM JobEntity j
    LEFT JOIN FuelConsumptionEntity fc ON fc.job = j
    WHERE j.isComplete = TRUE
""")
    Page<DoneJobTableInfoDto> findAllDoneJobTableInfo(Specification<JobEntity> spec, Pageable pageable);
}
