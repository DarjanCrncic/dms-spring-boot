package com.example.dms.repositories;

import com.example.dms.domain.DmsDocumentColumnPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentColumnPreferencesRepository extends JpaRepository<DmsDocumentColumnPreference, Integer>{
	
	List<DmsDocumentColumnPreference> findAllByUserUsername(String username);

	Optional<DmsDocumentColumnPreference> findByIdentifier(String string);

	Optional<DmsDocumentColumnPreference> findByIdentifierAndUserUsername(String identifier, String username);

}
