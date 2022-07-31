package com.example.dms.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.dms.security.configuration.acl.CustomBasePermission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

public enum Permissions {
	READ("READ", BasePermission.READ),
	WRITE("WRITE", BasePermission.WRITE),
	CREATE("CREATE", BasePermission.CREATE),
	VERSION("VERSION", CustomBasePermission.VERSION),
	DELETE("DELETE", BasePermission.DELETE),
	ADMINISTRATION("ADMINISTRATION", BasePermission.ADMINISTRATION);

	private final String value;
	private final Permission permission;
	
	private Permissions(String value, Permission permission) {
		this.value = value;
		this.permission = permission;
	}
	
	private static Map<String, Permission> map = new HashMap<>();
	private static Map<Integer, String> maskMap = new HashMap<>();
	
	static {
		for (Permissions perm : values()) {
			map.put(perm.value, perm.permission);
			maskMap.put(perm.permission.getMask(), perm.value);
		}
	}
	
	public static Permission getByString(String value) {
		return map.get(value);
	}
	
	public static List<Permission> getByStrings(Collection<String> values) {
		return values.stream().map(val -> map.get(val)).collect(Collectors.toList());
	}
	
	public static String getByMask(Integer value) {
		return maskMap.get(value);
	}
	
	public static List<String> getByMasks(Collection<Integer> values) {
		return values.stream().map(val -> maskMap.get(val)).collect(Collectors.toList());
	}
}
