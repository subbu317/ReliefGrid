package dev.reliefgrid.repo;
import dev.reliefgrid.model.CitizenRequest; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface CitizenRequestRepository extends JpaRepository<CitizenRequest,Long> {
 @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @Query("select r from CitizenRequest r where r.id = :id") java.util.Optional<CitizenRequest> locked(@Param("id") Long id);
}
