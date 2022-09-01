package com.example.dms.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@MappedSuperclass
@Setter
public class BaseEntity {

	@Id
	@Column(length=36)
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Type(type = "uuid-char")
	private UUID id;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime creationDate;
	
	@UpdateTimestamp
	private LocalDateTime modifyDate;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (id == null || o == null || getClass() != o.getClass()) return false;
		BaseEntity that = (BaseEntity) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
