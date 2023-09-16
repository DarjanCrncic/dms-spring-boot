package com.example.dms.domain.interfaces;

import com.example.dms.utils.TypeEnum;

public interface DmsNotifiable {

	String getName();
	Integer getLink();
	String getLinkName();

	TypeEnum getObjectType();
}
