package com.example.dms.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class DmsDocumentColumnPreference extends BaseEntity {
	
	private String identifier;
	private boolean displayed;
	private String title;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	@JsonIgnore
	@Default
	private DmsUser user = null;

}
