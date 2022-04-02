package com.example.dms.services.search;

import org.springframework.data.jpa.domain.Specification;

public interface Providable<T> {

	Specification<T> getNewInstance(SearchCriteria criteria);
}
