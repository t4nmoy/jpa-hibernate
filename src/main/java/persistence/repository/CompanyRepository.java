package persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import persistence.entity.Company;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCode(String code);
}
