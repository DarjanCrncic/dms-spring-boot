package com.example.dms.domain;

import com.example.dms.domain.security.AclAllowedClass;
import com.example.dms.utils.Constants;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DmsDocument extends BaseEntity implements AclAllowedClass{

	@ManyToOne(optional = false)
	@JoinColumn(name = "creator_id")
	@JsonIgnore
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
	@ElementCollection(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "dms_document_id")
	private List<String> keywords = new ArrayList<>();
	
	@ManyToOne(optional = false)
	@JoinColumn(name="type_id")
	@Default
	@JsonBackReference
	private DmsType type = null;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "parent_folder_id")
	@Default
	private DmsFolder parentFolder = null;

	// internal attributes
	@OneToOne(mappedBy = "document", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
	private DmsContent content;
	
	@Default
	@Type(type = "uuid-char")
	private UUID rootId = null;
	@Default
	@Type(type = "uuid-char")
	private UUID predecessorId = null;
	@Default
	private boolean immutable = false;
	@Default 
	private String version = "1";
	@Default
	private boolean branched = false;

	public void addCreator(DmsUser creator) {
		if (creator != null && !creator.getDocuments().contains(this) && this.getCreator() == null) {
			this.setCreator(creator);
			creator.getDocuments().add(this);
		}
	}
	
	public void addType(DmsType type) {
		if (type != null && !type.getDocuments().contains(this)) {
			this.setType(type);
			type.getDocuments().add(this);
		}
	}
	
	public void addParentFolder(DmsFolder folder) {
		if (folder != null && !folder.getDocuments().contains(this)) {
			this.setParentFolder(folder);
			folder.getDocuments().add(this);
		}
	}
}
