package persistence.repository;

import org.springframework.data.repository.NoRepositoryBean;
import persistence.query.CustomCriteria;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface ExtendedBaseRepository <T, ID extends Serializable> extends ExtendedJpaRepository<T, ID> {

    List<T> findAll(List<CustomCriteria> filters);
}
