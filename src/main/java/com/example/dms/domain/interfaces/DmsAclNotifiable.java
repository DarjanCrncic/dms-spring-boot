package com.example.dms.domain.interfaces;

import com.example.dms.domain.security.AclAllowedClass;

public interface DmsAclNotifiable extends  DmsNotifiable {
	AclAllowedClass getACLObjectForPermissions();
}
