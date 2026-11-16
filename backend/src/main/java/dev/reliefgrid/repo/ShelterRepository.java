package dev.reliefgrid.repo;
import dev.reliefgrid.model.Shelter; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface ShelterRepository extends JpaRepository<Shelter,Long> {}
