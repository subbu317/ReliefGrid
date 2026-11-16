package dev.reliefgrid.repo;
import dev.reliefgrid.model.Volunteer; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface VolunteerRepository extends JpaRepository<Volunteer,Long> { @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @Query("select v from Volunteer v where v.id = :id") java.util.Optional<Volunteer> locked(@Param("id") Long id);}
