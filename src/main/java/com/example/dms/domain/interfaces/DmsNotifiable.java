package com.example.dms.domain.interfaces;

import com.example.dms.utils.TypeEnum;

import java.util.UUID;

public interface DmsNotifiable {

	String getName();
	UUID getLink();
	String getLinkName();

	TypeEnum getObjectType();
}
