package com.example.dms.services.search.group;

import com.example.dms.domain.DmsGroup;
import com.example.dms.services.search.Providable;
import com.example.dms.services.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class GroupSpecProvider implements Providable<DmsGroup> {
	@Override
	public Specification<DmsGroup> getNewInstance(SearchCriteria criteria) {
		return new GroupSpecification(criteria);
	}
}
