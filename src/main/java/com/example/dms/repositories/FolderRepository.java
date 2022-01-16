package com.example.dms.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dms.domain.DmsFolder;

public interface FolderRepository extends JpaRepository<DmsFolder, UUID>{
	
	Optional<DmsFolder> findByPath(String path);

}
