package com.example.dms.services.search.document;

import org.springframework.data.jpa.domain.Specification;

import com.example.dms.domain.DmsDocument;
import com.example.dms.services.search.Providable;
import com.example.dms.services.search.SearchCriteria;

public class DocumentSpecProvider implements Providable<DmsDocument> {

	@Override
	public Specification<DmsDocument> getNewInstance(SearchCriteria criteria) {
		return new DocumentSpecification(criteria);
	}
}
