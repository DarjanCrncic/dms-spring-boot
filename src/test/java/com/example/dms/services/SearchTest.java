package com.example.dms.services;

import com.example.dms.domain.DmsDocument;
import com.example.dms.services.search.SpecificationBuilder;
import com.example.dms.services.search.document.DocumentSpecProvider;
import org.junit.jupiter.api.Test;

public class SearchTest {

	@Test
	void testSearch() {
		String search = "parentFolder~eq~013a8144-85cc-4dcb-b484-60cef134d564~and~" +
				"(immutable~eq~false~and~(objectName~like~se~or~creator~like~se~or~type~like~se~or~description~like~se))";

		SpecificationBuilder<DmsDocument> specBuilder = new SpecificationBuilder<>(new DocumentSpecProvider());
		specBuilder.parse(search);
	}
}
