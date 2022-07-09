package com.example.dms.domain.security;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DmsRole implements GrantedAuthority {
	
	private static final long serialVersionUID = -5873742027665612084L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    
	@Override
	public String getAuthority() {
		return name;
	}
}