package com.example.dms.domain;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.example.dms.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper=true)
@Entity
public class Document extends BaseEntity{

	@ManyToOne
	@JoinColumn(name = "creator_id")
	@NonNull
	@JsonIgnore
	private User creator;
	
	@NotBlank
	@Length(min = Constants.MINLENGTH, max = 32, message = "Ivalid object name length, document name must have atleast " 
			+ Constants.MINLENGTH + " characters.")
	@NonNull
	private String objectName;
	
	private Blob content = null;
	
}