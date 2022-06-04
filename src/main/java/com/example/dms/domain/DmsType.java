package com.example.dms.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import com.example.dms.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
	@Length(min = Constants.MINLENGTH, max = Constants.MAXLENGTH)
	@Column(unique = true)
	private String typeName;
	
	@OneToMany(mappedBy = "type")
	@Default
	@JsonManagedReference
	@JsonIgnore
	List<DmsDocument> documents = new ArrayList<>();
}
