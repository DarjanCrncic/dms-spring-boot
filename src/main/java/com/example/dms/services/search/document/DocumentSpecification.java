package com.example.dms.services.search.document;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsUser;
import com.example.dms.services.search.SearchCriteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentSpecification implements Specification<DmsDocument> {

	private static final long serialVersionUID = -3047798995945178009L;
	private SearchCriteria criteria;

    @Override
    public Predicate toPredicate
      (Root<DmsDocument> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    	
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                  root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else if (root.get(criteria.getKey()).getJavaType() == DmsUser.class) {
        		return builder.like(root.<DmsUser>get("creator").<String>get("username"), "%" + criteria.getValue() + "%");
        	} else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
