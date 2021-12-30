package com.example.dms.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
@Entity
public class Document extends BaseEntity{

	@ManyToOne
	@JoinColumn(name = "creator_id")
	private User creator;
	
	@NotBlank
	@Length(min = 5, max = 32)
	private String objectName;
}
