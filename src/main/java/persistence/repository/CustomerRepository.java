package persistence.repository;

import persistence.entity.Customer;

public interface CustomerRepository extends ExtendedBaseRepository<Customer, Long> {
    Customer findByName(String name);
}
