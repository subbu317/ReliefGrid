package dev.reliefgrid.repo;
import dev.reliefgrid.model.Assignment; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface AssignmentRepository extends JpaRepository<Assignment,Long> { java.util.List<Assignment> findByPlanId(Long id);}
