package com.example.dms.services.search;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public abstract class BasicSearchSpecification {
	
	public static final String EQUALS = ":";
	public static final String NOT_EQUALS_OPERATION = "!";
	public static final String GREATER = ">";
	public static final String LESS = "<";
	public static final String LESS_OR_EQUALS = "<=";
	public static final String GREATER_OR_EQUALS = ">=";

    public Predicate toPredicateBasic(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder builder, SearchCriteria criteria) {
    	
        if (criteria.getOperation().equalsIgnoreCase(GREATER)) {
            return builder.greaterThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase(GREATER_OR_EQUALS)) {
        	return builder.greaterThanOrEqualTo(root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase(LESS)) {
            return builder.lessThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase(LESS_OR_EQUALS)) {
        	return builder.lessThanOrEqualTo(root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase(EQUALS)) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                  root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else if (root.get(criteria.getKey()).getJavaType().getTypeName().equals("boolean")) {
				return builder.equal(root.get(criteria.getKey()), Boolean.parseBoolean(criteria.getValue().toString()));
			} else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        } 
        else if (criteria.getOperation().equalsIgnoreCase(NOT_EQUALS_OPERATION)) {
        	return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
        }
        return null;
    }
}
