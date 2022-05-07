package com.example.dms.utils;

import java.util.Optional;

import org.springframework.data.domain.Sort;

import com.example.dms.api.dtos.SortDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class Utils {
	public static String stringify(Object object) throws JsonProcessingException {
		return new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
				.writeValueAsString(object);
	}

	public static Sort toSort(Optional<SortDTO> sort) {
		if (sort.isEmpty()) {
			return Sort.by(Sort.Direction.DESC, "crationDate");
		}
		return Sort.by(sort.get().getDirection().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
				StringUtils.snakeToCammel(sort.get().getActive()));
	}
}
