package persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import persistence.exception.NoSuchEntityException;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface ExtendedJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    default T findMust(ID id) {
        Optional<T> obj = findById(id);
        if (!obj.isPresent()) {
            throw new NoSuchEntityException("no entity of type found with id " + id);
        }
        return obj.get();
    }
}
