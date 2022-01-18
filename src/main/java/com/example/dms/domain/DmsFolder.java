package com.example.dms.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

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
@Entity
public class DmsFolder extends BaseEntity {

	@NotEmpty
	@Column(unique = true)
	@Pattern(regexp = Constants.FOLDER_PATH_REGEX)
	private String path;

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

}
