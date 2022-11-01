package com.example.dms.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class DmsNotification extends BaseEntity {

	private String message;
	private UUID linkTo;
	private boolean seen = false;

	// TODO: Clear orphaned records or improve to serve as history records
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
			name = "users_notifications",
			joinColumns = @JoinColumn(name = "notification_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<DmsUser> recipients;
}
