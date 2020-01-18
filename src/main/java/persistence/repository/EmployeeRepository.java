package persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import persistence.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
