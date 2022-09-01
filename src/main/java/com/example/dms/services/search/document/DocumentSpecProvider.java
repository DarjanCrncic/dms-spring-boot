package com.example.dms.services.search.document;

import com.example.dms.domain.DmsDocument;
import com.example.dms.services.search.Providable;
import com.example.dms.services.search.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class DocumentSpecProvider implements Providable<DmsDocument> {

	@Override
	public Specification<DmsDocument> getNewInstance(SearchCriteria criteria) {
		return new DocumentSpecification(criteria);
	}
}
