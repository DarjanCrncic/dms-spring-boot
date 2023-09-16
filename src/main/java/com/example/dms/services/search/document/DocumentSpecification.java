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

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentSpecification extends BasicSearchSpecification implements Specification<DmsDocument>  {

	private static final long serialVersionUID = -3047798995945178009L;
	private SearchCriteria criteria;

    @Override
    public Predicate toPredicate
      (Root<DmsDocument> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		if (root.get(criteria.getKey()).getJavaType() == DmsUser.class) {
			criteria.setKey("creator.username");
		} else if (root.get(criteria.getKey()).getJavaType() == DmsType.class) {
			criteria.setKey("type.typeName");
		} else if (root.get(criteria.getKey()).getJavaType() == DmsFolder.class) {
			criteria.setKey("parentFolder.id");
		}

        return super.toPredicateBasic(root, query, builder, criteria);
    }
}
