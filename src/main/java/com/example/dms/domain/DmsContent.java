package com.example.dms.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DmsContent extends BaseEntity{
	@Lob
	@Default
	private byte[] content = null;
	@Default
	private Long contentSize = 0l;
	@Default
	private String contentType = null;
	@Default
	private String originalFileName = null;
	
	@OneToOne
	@JoinColumn(name = "document_id")
	private DmsDocument document;
}
