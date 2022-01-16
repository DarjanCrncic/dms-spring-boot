package com.example.dms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@Builder
@Entity
public class DmsType extends BaseEntity{
	
	@NotEmpty
	@Length(min = 5, max = 32)
	@Column(unique = true)
	private String typeName;
	
}
