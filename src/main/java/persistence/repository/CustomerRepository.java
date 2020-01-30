package persistence.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import persistence.entity.Customer;

import java.util.List;

@SuppressWarnings("ALL")
public interface CustomerRepository extends ExtendedBaseRepository<Customer, Long> {
    Customer findByName(String name);

    @Query(value = "select name from customer where name = :name and type in :types", nativeQuery = true)
    List<String> findByNameAndTypesIn(@Param("name") String name, @Param("types") List<String> contactTypes);

    @Query(value = "update customer set type = ?2 where company_id = ?1", nativeQuery = true)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void changeType(Long companyId, String customerType);
}
