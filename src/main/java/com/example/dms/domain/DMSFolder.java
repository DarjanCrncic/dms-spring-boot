package com.example.dms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.example.dms.utils.Constants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
@Builder
@Entity
public class DMSFolder extends BaseEntity{
	
	@NotEmpty
	@Column(unique = true)
	@Pattern(regexp = Constants.FOLDER_PATH_REGEX)
	private String path;
}
