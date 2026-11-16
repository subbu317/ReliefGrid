package dev.reliefgrid.repo;
import dev.reliefgrid.model.AllocationPlan; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface AllocationPlanRepository extends JpaRepository<AllocationPlan,Long> {
 @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @Query("select p from AllocationPlan p where p.id = :id") java.util.Optional<AllocationPlan> locked(@Param("id") Long id);
}
