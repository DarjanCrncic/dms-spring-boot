package com.example.dms.services.search.user;

import com.example.dms.domain.DmsUser;
import com.example.dms.services.search.BasicSearchSpecification;
import com.example.dms.services.search.SearchCriteria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSpecification extends BasicSearchSpecification implements Specification<DmsUser> {

	private static final long serialVersionUID = -3047798995945178009L;
	private SearchCriteria criteria;

	@Override
	public Predicate toPredicate
			(Root<DmsUser> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		return super.toPredicateBasic(root, query, builder, criteria);
	}
}
