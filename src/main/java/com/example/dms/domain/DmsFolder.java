package com.example.dms.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.example.dms.domain.security.AclAllowedClass;
import com.example.dms.utils.Constants;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false, exclude = {"parentFolder", "subfolders"})
@Builder
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "parent_folder_id" }) })
@Entity
public class DmsFolder extends BaseEntity implements AclAllowedClass{

	@NotEmpty
	@Pattern(regexp = Constants.FOLDER_NAME_REGEX)
	private String name;

	@ManyToOne
	@JoinColumn(name = "parent_folder_id")
	@Builder.Default
	@JsonBackReference("subfolders")
	private DmsFolder parentFolder = null;

	@OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	@JsonManagedReference("subfolders")
	private List<DmsFolder> subfolders = new ArrayList<>();
	
	@OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	@JsonManagedReference("documents")
	@ToStringExclude
	private List<DmsDocument> documents = new ArrayList<>();

	public void addParentFolder(DmsFolder parentFolder) {
		if (!parentFolder.getSubfolders().contains(this)) {
			this.setParentFolder(parentFolder);
			parentFolder.getSubfolders().add(this);
		}
	}
	
	public void addDocument(DmsDocument document) {
		if (!this.getDocuments().contains(document)) {
			document.setParentFolder(this);
			this.getDocuments().add(document);
		}
	}
}
