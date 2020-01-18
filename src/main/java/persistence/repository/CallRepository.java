package persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import persistence.entity.Call;

public interface CallRepository extends JpaRepository<Call, Long> {
}
