package com.example.dms.repositories;

import com.example.dms.domain.DmsDocumentColumnPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentColumnPreferencesRepository extends JpaRepository<DmsDocumentColumnPreference, UUID>{
	
	public List<DmsDocumentColumnPreference> findAllByUserUsername(String username);

	public Optional<DmsDocumentColumnPreference> findByIdentifier(String string);

	public Optional<DmsDocumentColumnPreference> findByIdentifierAndUserUsername(String identifier, String username);

}
