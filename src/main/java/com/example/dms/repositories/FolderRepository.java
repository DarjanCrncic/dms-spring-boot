package com.example.dms.repositories;

import com.example.dms.domain.DmsFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<DmsFolder, Integer>{
	
	@Query(nativeQuery = true, value = "SELECT folder.id FROM dms_folder folder "
			+ "JOIN acl_object_identity aoi on aoi.object_id_identity = folder.id "
			+ "JOIN acl_entry entry on entry.acl_object_identity = aoi.id "
			+ "JOIN acl_class class on class.id = aoi.object_id_class "
			+ "JOIN acl_sid sid on sid.id = entry.sid "
			+ "WHERE class.class = 'com.example.dms.domain.DmsFolder' and sid.sid = ?1 and entry.mask = 1 and entry.granting = true")
	List<Integer> getVisibleFolderIds(String username);
	
	Optional<DmsFolder> findByNameAndParentFolderId(String name, Integer parentFolderId);
	
	Optional<DmsFolder> findByName(String name);
}
