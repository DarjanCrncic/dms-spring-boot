package com.example.dms.utils;

import com.example.dms.api.dtos.SortDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Log4j2
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

	private static final List<String> supportedDateFormats = List.of("yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy");
	public static LocalDateTime parseDateFromString(String date) {
		for (String format : supportedDateFormats) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
				return LocalDate.parse(date, formatter).atStartOfDay();
			} catch (DateTimeParseException e) {
				log.debug("Date not of type: {}", format);
			}
		}
		return null;
	}
}
