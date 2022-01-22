package com.example.dms.domain;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.example.dms.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
@Builder
@Entity
public class DmsDocument extends BaseEntity{

	@ManyToOne
	@JoinColumn(name = "creator_id")
	@JsonIgnore
	@NonNull
	private DmsUser creator;
	
	@NotBlank
	@Length(min = Constants.MINLENGTH, max = 32, message = "Ivalid object name length, document name must have atleast " 
			+ Constants.MINLENGTH + " characters.")
	@NonNull
	private String objectName;
	
	@Builder.Default
	private String description = null; 

	@Lob
	@Default
	private byte[] content = null;
	@Default
	private Long contentSize = 0l;
	@Default
	private String contentType = null;
	@Default
	private String originalFileName = null;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "parent_folder_id")
	@Default
	private DmsFolder parentFolder = null;
	
	@Default
	private UUID rootId = null;
	@Default
	private UUID predecessorId = null;
	@Default
	private boolean imutable = false;
	@Default 
	private int version = 1;
}
