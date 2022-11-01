package com.example.dms.api.mappers;

import com.example.dms.api.dtos.notification.DmsNotificationDTO;
import com.example.dms.domain.DmsNotification;
import com.example.dms.domain.DmsUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface NotificationMapper {

	NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

	DmsNotificationDTO entityToDto(DmsNotification notification);

	List<DmsNotificationDTO> entityListToDtoList(List<DmsNotification> list);

	default String userToUsername(DmsUser user) {
		return user.getUsername();
	}
}
