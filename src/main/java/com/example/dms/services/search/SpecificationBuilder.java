package com.example.dms.services.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.jpa.domain.Specification;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
public class SpecificationBuilder<T> {
	
	public static final String OR_OPERATOR = "~";
	public static final String AND_OPERATOR = ",";
    
	private Providable<T> specProvider;
    private final List<SearchCriteria> params = new ArrayList<>();

    public SpecificationBuilder<T> with(String key, String operation, Object value, String operator) {
        params.add(new SearchCriteria(key, operation, value, operator));
        return this;
    }

    public Specification<T> build() {
        if (params.isEmpty()) {
            return null;
        }

        List<Specification<T>> specs = new ArrayList<>();
        for (SearchCriteria criteria : params) {
        	specs.add(specProvider.getNewInstance(criteria));
        }
        
        Specification<T> result = specs.get(0);

        for (int i = 1; i < specs.size(); i++) {
        	if (params.get(i-1).getOperator().equalsIgnoreCase(OR_OPERATOR))
        		result = Specification.where(result).or(specs.get(i));
        	else
        		result = Specification.where(result).and(specs.get(i));
        }       
        log.debug(result);
        return result;
    }
    
    public Specification<T> parse(String search) {
		Pattern pattern = Pattern.compile("([\\w|\\/]+?)(:|<|>|!|<=|>=)([\\w|\\/]+?)(,|~|$)");
        Matcher matcher = pattern.matcher(search + AND_OPERATOR);
        while (matcher.find()) {
            this.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }
        
        return this.build();
	}
}