package com.example.dms.services.search.user;

import com.example.dms.domain.DmsUser;
import com.example.dms.services.search.Providable;
import com.example.dms.services.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecProvider implements Providable<DmsUser> {
	@Override
	public Specification<DmsUser> getNewInstance(SearchCriteria criteria) {
		return new UserSpecification(criteria);
	}
}
