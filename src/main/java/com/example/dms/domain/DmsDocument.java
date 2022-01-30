package com.example.dms.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import com.example.dms.utils.Constants;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@Builder
@Entity
public class DmsDocument extends BaseEntity{

	@ManyToOne
	@JoinColumn(name = "creator_id")
	@JsonIgnore
	@NotNull
	@Default
	private DmsUser creator = null;
	
	@NotBlank
	@Length(min = Constants.MINLENGTH, max = 32, message = "Ivalid object name length, document name must have atleast " 
			+ Constants.MINLENGTH + " characters.")
	@NotEmpty
	private String objectName;
	
	@Default
	private String description = null; 
	
	@Default
	@ElementCollection
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "dms_document_id")
	@ToString.Exclude
	private List<String> keywords = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name="type_id")
	@Default
	@JsonBackReference
	private DmsType type = null;
	
	@ManyToOne
	@JoinColumn(name = "parent_folder_id")
	@Default
	private DmsFolder parentFolder = null;

	// internal attributes
	@Lob
	@Default
	private byte[] content = null;
	@Default
	private Long contentSize = 0l;
	@Default
	private String contentType = null;
	@Default
	private String originalFileName = null;
	
	@Default
	private UUID rootId = null;
	@Default
	private UUID predecessorId = null;
	@Default
	private boolean imutable = false;
	@Default 
	private int version = 1;
}
