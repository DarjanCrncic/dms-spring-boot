package com.example.dms.services.impl;

import com.example.dms.domain.DmsNotification;
import com.example.dms.domain.DmsUser;
import com.example.dms.domain.interfaces.DmsAclNotifiable;
import com.example.dms.domain.security.AclAllowedClass;
import com.example.dms.repositories.NotificationRepository;
import com.example.dms.repositories.UserRepository;
import com.example.dms.services.DmsAclService;
import com.example.dms.services.MessagingService;
import com.example.dms.services.NotificationService;
import com.example.dms.utils.ActionEnum;
import com.example.dms.utils.DmsSecurityContext;
import com.example.dms.utils.NotificationUtils;
import com.example.dms.utils.Roles;
import com.example.dms.utils.exceptions.DmsNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final MessagingService messagingService;
	private final UserRepository userRepository;
	private final DmsAclService aclService;

	@Override
	public DmsNotification findById(Integer id) {
		return notificationRepository.findById(id).orElseThrow(DmsNotFoundException::new);
	}

	@Override
	public DmsNotification save(DmsNotification notification) {
		messagingService.notify(notification);
		return notificationRepository.save(notification);
	}

	@Override
	public List<DmsNotification> getAllForUser(Integer userId) {
		Sort sort = Sort.by(Sort.Direction.DESC, "creationDate");
		return notificationRepository.findAllByRecipientIdIn(Collections.singletonList(userId), sort);
	}

	@Override
	public void deleteAllForUser(Integer userId) {
		notificationRepository.deleteAllByRecipientIdIn(Collections.singletonList(userId));
	}

	@Override
	public DmsNotification markAsSeen(Integer id) {
		log.info("Marking notification as seen: {}", id);
		DmsNotification notification = this.findById(id);
		notification.setSeen(true);
		return notificationRepository.save(notification);
	}

	@Override
	public void createAclNotification(DmsAclNotifiable object, ActionEnum action) {
		List<DmsUser> recipients = userRepository.findByRoleName(Roles.ROLE_ADMIN.name());

		AclAllowedClass objectForNotification = object.getACLObjectForPermissions();
		if (objectForNotification == null) {
			log.warn("No recipients to inform, object for notification not found.");
			return;
		}
		Set<String> usernamesAndIdentifiers = aclService.getRecipients(objectForNotification);

		recipients.addAll(userRepository.findAllByUsernameInOrGroupsIdentifierIn(usernamesAndIdentifiers, usernamesAndIdentifiers));
		List<DmsUser> uniqueRecipients = recipients.stream()
				.filter(user -> !user.getUsername().equals(DmsSecurityContext.getUsername()))
				.collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(DmsUser::getUsername))), ArrayList::new));

		uniqueRecipients.forEach(recipient -> {
			DmsNotification notification = DmsNotification.builder()
					.message(NotificationUtils.buildMessage(object.getName(), object.getLinkName(), object.getObjectType(), action))
					.recipient(recipient)
					.seen(false)
					.linkTo(object.getLink()).build();
			save(notification);
		});
	}
}
