package persistence.repository;

import persistence.entity.Department;

import java.util.Optional;

public interface DepartmentRepository extends ExtendedJpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);
}
