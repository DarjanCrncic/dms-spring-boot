package com.example.dms.services.search.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.dms.domain.DmsDocument;
import com.example.dms.services.search.SearchCriteria;

public class DocumentSpecificationBuilder {
    
    private final List<SearchCriteria> params = new ArrayList<>();

    public DocumentSpecificationBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<DmsDocument> build() {
        if (params.isEmpty()) {
            return null;
        }

        List<Specification<DmsDocument>> specs = new ArrayList<>();
        for (SearchCriteria criteria : params) {
        	specs.add(new DocumentSpecification(criteria));
        }
        
        Specification<DmsDocument> result = specs.get(0);

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(specs.get(i));
        }       
        
        return result;
    }
}