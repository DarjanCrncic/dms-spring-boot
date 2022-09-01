package com.example.dms.services.search.document;

import com.example.dms.domain.DmsDocument;
import com.example.dms.domain.DmsFolder;
import com.example.dms.domain.DmsType;
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
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentSpecification extends BasicSearchSpecification implements Specification<DmsDocument>  {

	private static final long serialVersionUID = -3047798995945178009L;
	private SearchCriteria criteria;

    @Override
    public Predicate toPredicate
      (Root<DmsDocument> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    	
        if (criteria.getOperation().equalsIgnoreCase(BasicSearchSpecification.EQUALS)) {
            if (root.get(criteria.getKey()).getJavaType() == DmsUser.class) {
        		return builder.like(root.<DmsUser>get("creator").<String>get("username"), "%" + criteria.getValue() + "%");
        	} else if (root.get(criteria.getKey()).getJavaType() == DmsType.class) {
        		return builder.like(root.<DmsType>get("type").<String>get("typeName"), "%" + criteria.getValue() + "%");
        	} else if (root.get(criteria.getKey()).getJavaType() == DmsFolder.class) {
        		return builder.equal(root.<DmsFolder>get("parentFolder").<UUID>get("id"), UUID.fromString((String) criteria.getValue()));
        	}
        }
        return super.toPredicateBasic(root, query, builder, criteria);
    }
}
