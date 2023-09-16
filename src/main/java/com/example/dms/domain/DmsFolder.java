package com.example.dms.domain;

import com.example.dms.domain.interfaces.DmsAclNotifiable;
import com.example.dms.domain.security.AclAllowedClass;
import com.example.dms.utils.Constants;
import com.example.dms.utils.TypeEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "parent_folder_id"})})
@Entity
public class DmsFolder extends BaseEntity implements AclAllowedClass, DmsAclNotifiable {

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

	public String getName() { return name; }

	public Integer getLink() { return parentFolder.getId(); }

	public String getLinkName() { return parentFolder.getName(); }

	public AclAllowedClass getACLObjectForPermissions() { return parentFolder; }

	public TypeEnum getObjectType() { return TypeEnum.FOLDER; }

	public boolean isRoot() {
		return Constants.ROOT.equals(name);
	}
}
