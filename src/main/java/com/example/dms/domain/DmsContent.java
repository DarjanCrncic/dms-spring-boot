package com.example.dms.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DmsContent extends BaseEntity{
	@Lob
	@Default
	private byte[] content = null;
	@Default
	private Long contentSize = 0L;
	@Default
	private String contentType = null;
	@Default
	private String originalFileName = null;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "document_id")
	private DmsDocument document;
}
