package persistence.repository;

import persistence.entity.Company;

import java.util.Optional;

public interface CompanyRepository extends ExtendedJpaRepository<Company, Long> {
    Optional<Company> findByCode(String code);
}
