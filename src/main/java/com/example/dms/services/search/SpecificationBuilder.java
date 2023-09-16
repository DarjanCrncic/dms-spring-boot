package com.example.dms.services.search;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class SpecificationBuilder<T> {

	public static final String OR_OPERATOR = "or";
	public static final String AND_OPERATOR = "and";
	public static final String SEPARATOR = "~";
	public static final String searchRegex = "(\\b(?!and|or|AND|OR\\b)[\\w\\.]+)~(\\w+)~([\\w|\\-|\\/|\\,|\\.]+)";
	public static final String numRegex = "(\\d+)~?(\\b(and|or|AND|OR\\b))?";
	public static final String complexNumExpression = "(\\d+)~(\\b(and|or|AND|OR\\b))~(\\d+)";
	public static final String extraParenthesesRegex = "\\((\\d+)\\)";


	Map<Integer, String> parsedSearchMap = new LinkedHashMap<>(); // stores string subcomponents of the search
	Map<Integer, SearchCriteria> criteriaMap = new HashMap<>(); // stores search criteria objects
	Map<Integer, Specification<T>> specMap = new LinkedHashMap<>(); // stores generated specifications


	private final Providable<T> specProvider;

	public SpecificationBuilder(Providable<T> specProvider) {
		this.specProvider = specProvider;
	}

	public Specification<T> parse(String search) {
		String parsingResult = parseSearchString(search);
		return buildSpecFromSymbolicString(parsingResult);
	}

	protected String parseSearchString(String search) {
		Pattern pattern = Pattern.compile(searchRegex);
		Matcher matcher = pattern.matcher(search);
		Integer counter = 1;

		// find all strings that match search criteria
		while(matcher.find()) {
			parsedSearchMap.put(counter, matcher.group(1) + SEPARATOR + matcher.group(2) + SEPARATOR + matcher.group(3));
			criteriaMap.put(counter, new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
			counter++;
		}
		// replace all criteria in the search with respective numeric keys
		for (Integer key: parsedSearchMap.keySet()) {
			search = search.replace(parsedSearchMap.get(key), key.toString());
		}
		search = search.replaceAll(extraParenthesesRegex, "$1");
		log.info("symbolic search after parsing: {}", search);

		long parenthesisCount = validateAndCountParenthesis(search);
		log.info("parenthesis count: {}", parenthesisCount);
		// find all substrings in brackets, recursively replace all substrings with other symbolic search strings
		while(parenthesisCount-- > 0) {
			String found = findDeepestBracket(search);
			parsedSearchMap.put(counter, found.substring(1, found.length()-1));
			search = search.replace(found, counter.toString());
			counter++;
		}
		log.info("search after parenthesis replacement: {}", search);
		log.info("number of total search subcomponents found: {}", counter - 1);

		// build specifications from all keys in map
		for (Integer key: parsedSearchMap.keySet()) {
			String val = parsedSearchMap.get(key);
			if (val.matches(complexNumExpression) || criteriaMap.get(key) == null) {
				specMap.put(key, buildSpecFromSymbolicString(val));
			} else {
				specMap.put(key, specProvider.getNewInstance(criteriaMap.get(key)));
			}
		}

		return search;
	}

	private long validateAndCountParenthesis(String search) {
		long numOfLB = search.chars().filter(ch -> ch == '(').count();
		long numOfRB = search.chars().filter(ch -> ch == ')').count();
		if (numOfLB != numOfRB) {
			throw new IllegalArgumentException("Invalid search string: parentheses not matching.");
		}
		return numOfRB;
	}

	private Specification<T> buildSpecFromSymbolicString(String val) {
		Pattern patternNum = Pattern.compile(numRegex);
		Matcher matcherNum = patternNum.matcher(val);

		Specification<T> result = null;
		String operation = "";
		while (matcherNum.find()) {
			Integer index = Integer.valueOf(matcherNum.group(1));
			if (specMap.containsKey(index)) { // Check if index exists in the map
				if (result == null) {
					result = Specification.where(specMap.get(index));
				} else {
					if (operation.equalsIgnoreCase(AND_OPERATOR)) {
						result = Specification.where(result).and(specMap.get(index));
					} else if (operation.equalsIgnoreCase(OR_OPERATOR)) {
						result = Specification.where(result).or(specMap.get(index));
					}
				}
				operation = matcherNum.group(2);
			} else {
				throw new IllegalArgumentException("Invalid symbolic string, index " + index + " not found in map.");
			}
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