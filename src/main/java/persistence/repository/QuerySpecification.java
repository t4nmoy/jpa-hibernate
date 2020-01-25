package persistence.repository;

import org.springframework.data.jpa.domain.Specification;
import persistence.query.CustomCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class QuerySpecification<T> implements Specification<T> {

    private List<CustomCriteria> criteriaList;

    public QuerySpecification() {
        this.criteriaList = new ArrayList<>();
    }

    public void add(CustomCriteria criteria) {
        this.criteriaList.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}