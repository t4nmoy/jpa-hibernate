package persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import persistence.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
