package persistence.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ContainsOperation<T> implements CriteriaOperation<T> {
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaBuilder builder, CustomCriteria criteria) {
        return builder.in(root.get(criteria.getKey())).value(criteria.getValue());
    }
}
