package com.example.dms.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@MappedSuperclass
@EqualsAndHashCode
@Setter
public class BaseEntity {

	@Id
	@Column(length=16)
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private UUID id;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime creationDate;
	
	@UpdateTimestamp
	private LocalDateTime modifyDate;
}
