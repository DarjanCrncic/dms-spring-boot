package com.example.dms.services;

import com.example.dms.domain.DmsDocument;
import com.example.dms.services.search.SpecificationBuilder;
import com.example.dms.services.search.document.DocumentSpecProvider;
import org.junit.jupiter.api.Test;

public class SearchTest {

	@Test
	void testSearch() {
		String search = "parentFolder:013a8144-85cc-4dcb-b484-60cef134d564,(immutable:false~(objectName:se~creator:se~type:se~description:se))";

		SpecificationBuilder<DmsDocument> specBuilder = new SpecificationBuilder<>(new DocumentSpecProvider());
		specBuilder.parse(search);
	}
}
