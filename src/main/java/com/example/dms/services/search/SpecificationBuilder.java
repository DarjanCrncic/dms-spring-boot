package com.example.dms.services.search;

import com.example.dms.utils.exceptions.BadRequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class SpecificationBuilder<T> {
	
	public static final String OR_OPERATOR = "~";
	public static final String AND_OPERATOR = ",";
	public static final String searchRegex = "(\\w+)(:|<|>|!|<=|>=)([\\w|\\-|\\/]+)";
	public static final String numRegex = "(\\d)([,|~]?)";

	Map<String, String> map = new LinkedHashMap<>();
	Map<String, SearchCriteria> criteriaMap = new HashMap<>();
	Map<String, Specification<T>> specMap = new LinkedHashMap<>();


	private final Providable<T> specProvider;

	public SpecificationBuilder(Providable<T> specProvider) {
		this.specProvider = specProvider;
	}

	public Specification<T> parse(String search) {
		long numOfLB = search.chars().filter(ch -> ch == '(').count();
		long numOfRB = search.chars().filter(ch -> ch == ')').count();
		if (numOfLB != numOfRB) throw new BadRequestException("Search string invalid, parenthesis not matching.");

		Pattern pattern = Pattern.compile(searchRegex);
		Matcher matcher = pattern.matcher(search);
		Integer counter = 1;

		// find all strings that match search criteria
		while(matcher.find()) {
			map.put(counter.toString(), matcher.group(1) + matcher.group(2) + matcher.group(3));
			criteriaMap.put(counter.toString(), new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
			counter++;
		}
		// replace all criteria in the search with respective numeric keys
		for (String key: map.keySet()) {
			search = search.replace(map.get(key), key);
		}
		log.info(search);

		// find all substrings in brackets, recursively replace all substrings with other symbolic search strings
		while(search.contains("(") || search.contains(")")) {
			String found = findDeepestBracket(search);
			map.put(counter.toString(), found.substring(1, found.length()-1));
			search = search.replace(found, counter.toString());
			counter++;
		}

		// build specifications from all keys in map
		for (String key: map.keySet()) {
			String val = map.get(key);
			if (val.matches(searchRegex)) {
				specMap.put(key, specProvider.getNewInstance(criteriaMap.get(key)));
			} else {
				specMap.put(key, buildSpecFromSymbolicString(val));
			}
		}

		return buildSpecFromSymbolicString(search);
	}

	private Specification<T> buildSpecFromSymbolicString(String val) {
		Pattern patternNum = Pattern.compile(numRegex);
		Matcher matcherNum = patternNum.matcher(val);

		Specification<T> result = null;
		String operation = "";
		while (matcherNum.find()) {
			String index = matcherNum.group(1);
			if (result == null) {
				result = Specification.where(specMap.get(index));
			} else {
				if (operation.equalsIgnoreCase(AND_OPERATOR)) {
					result = Specification.where(result).and(specMap.get(index));
				} else {
					result = Specification.where(result).or(specMap.get(index));
				}
			}
			operation = matcherNum.group(2);
		}
		return result;
	}

	private String findDeepestBracket(String search) {
		char[] chars = search.toCharArray();
		int start = 0;
		int end = search.length();
		for (int i=0; i<chars.length; i++) {
			if (chars[i] == '(') {
				start = i;
			}
			if (chars[i] == ')') {
				end = i;
				break;
			}
		}
		return search.substring(start, end+1);
	}
}