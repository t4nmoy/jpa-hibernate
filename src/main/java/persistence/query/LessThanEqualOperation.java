package persistence.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@SuppressWarnings("unchecked")
public class LessThanEqualOperation<T> implements CriteriaOperation<T> {
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaBuilder builder, CustomCriteria criteria) {
        return builder.lessThanOrEqualTo(root.get(criteria.getKey()), (Comparable) criteria.getValue());
    }
}
