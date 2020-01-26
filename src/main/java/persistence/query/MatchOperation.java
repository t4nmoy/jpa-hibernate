package persistence.query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class MatchOperation<T> implements CriteriaOperation<T> {
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaBuilder builder, CustomCriteria criteria) {
        return builder.like(
                builder.lower(root.get(criteria.getKey())),
                "%" + criteria.getValue().toString().toLowerCase() + "%");
    }
}
