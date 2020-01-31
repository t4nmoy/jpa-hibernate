package persistence.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Query(value = "update customer set number = (select next_no.no from " +
            "(select ifnull(max(number), 0) + 1 as no from customer) next_no) " +
            "where id = ?1", nativeQuery = true)
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    void updateNumber(Long customerId);

    @Query("select customer from Customer customer")
    List<Customer> findAllSortByName(Sort sort);

    @Query(
            value = "SELECT * FROM customer",
            countQuery = "SELECT count(*) FROM customer",
            nativeQuery = true
    )
    List<Customer> findAllWithPagination(Pageable pageable);
}
