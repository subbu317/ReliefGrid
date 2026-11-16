package dev.reliefgrid.repo;
import dev.reliefgrid.model.AuditLog; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {}
