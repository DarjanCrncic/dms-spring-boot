package com.example.dms.services.search;

import com.example.dms.utils.StringUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
    private String operator;
	
    public SearchCriteria(String key, String operation, Object value, String operator) {
		super();
		this.key = StringUtils.snakeToCammel(key);
		this.operation = operation;
		this.value = value;
		this.operator = operator;
    }
    
}