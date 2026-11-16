package dev.reliefgrid.repo;
import dev.reliefgrid.model.SyncRecord; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface SyncRecordRepository extends JpaRepository<SyncRecord,Long> { java.util.Optional<SyncRecord> findBySyncKey(String key);}
