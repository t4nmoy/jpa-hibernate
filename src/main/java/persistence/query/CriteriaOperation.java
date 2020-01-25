package persistence.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface CriteriaOperation<T> {

    Predicate toPredicate(Root<T> root, CriteriaBuilder builder, CustomCriteria criteria);
}
