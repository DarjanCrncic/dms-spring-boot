package com.example.dms.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.dms.domain.DmsFolder;

public interface FolderRepository extends JpaRepository<DmsFolder, UUID>{
	
	Optional<DmsFolder> findByPath(String path);

	@Query(nativeQuery = true, value = "SELECT folder.id FROM dms_folder folder "
			+ "JOIN acl_object_identity aoi on aoi.object_id_identity = folder.id "
			+ "JOIN acl_entry entry on entry.acl_object_identity = aoi.id "
			+ "JOIN acl_class class on class.id = aoi.object_id_class "
			+ "JOIN acl_sid sid on sid.id = entry.sid "
			+ "WHERE class.class = 'com.example.dms.domain.DmsFolder' and sid.sid = ?1 and entry.mask = 1 and entry.granting = true")
	List<UUID> getVissibleFolderIds(String username);
}
