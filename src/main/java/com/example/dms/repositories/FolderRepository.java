package com.example.dms.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dms.domain.DMSFolder;

public interface FolderRepository extends JpaRepository<DMSFolder, UUID>{
	
	Optional<DMSFolder> findByPath(String path);

}
