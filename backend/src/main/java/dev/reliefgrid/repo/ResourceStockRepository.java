package dev.reliefgrid.repo;
import dev.reliefgrid.model.ResourceStock; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface ResourceStockRepository extends JpaRepository<ResourceStock,Long> { @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @Query("select s from ResourceStock s where s.id = :id") java.util.Optional<ResourceStock> locked(@Param("id") Long id);}
