package com.example.dms.domain.security;

import com.example.dms.domain.DmsUser;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DmsRole implements GrantedAuthority {
	
	private static final long serialVersionUID = -5873742027665612084L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    private String name;
    
	@Override
	public String getAuthority() {
		return name;
	}

	@ManyToMany(mappedBy = "roles")
	private Set<DmsUser> users = new HashSet<>();
}