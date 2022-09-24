package com.example.dms.utils;

import com.example.dms.api.dtos.SortDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.data.domain.Sort;

public class Utils {
	public static String stringify(Object object) throws JsonProcessingException {
		return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
				.writeValueAsString(object);
	}

	public static Sort toSort(SortDTO sort) {
		if (sort == null || StringUtils.isFalse(sort.getDirection()) || StringUtils.isFalse(sort.getActive())) {
			return Sort.by(Sort.Direction.DESC, "creationDate");
		}
		return Sort.by(sort.getDirection().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
				StringUtils.snakeToCammel(sort.getActive()));
	}
}
