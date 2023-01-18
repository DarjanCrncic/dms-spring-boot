package com.example.dms.domain;

import com.example.dms.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DmsType extends BaseEntity{
	
	@NotEmpty
	@Length(min = Constants.MIN_LENGTH_4, max = Constants.MAX_LENGTH_32)
	@Column(unique = true)
	private String typeName;
	
	@OneToMany(mappedBy = "type")
	@Default
	@JsonManagedReference
	@JsonIgnore
	List<DmsDocument> documents = new ArrayList<>();
}
