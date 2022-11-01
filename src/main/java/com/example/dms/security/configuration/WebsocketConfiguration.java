package com.example.dms.security.configuration;

import com.example.dms.security.DmsUserDetailsService;
import com.example.dms.security.JwtUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Log4j2
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private DmsUserDetailsService userDetailsService;
	@Value("${allowed.cors.origins}")
	private String allowedCorsOrigins;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/documents", "/folders", "/notifications");
		config.setApplicationDestinationPrefixes("/dms");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/dms-websocket")
				.setAllowedOrigins(allowedCorsOrigins.split(","));
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor =
						MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					List<String> tokenList = accessor.getNativeHeader("Authorization");
					if (tokenList == null || tokenList.isEmpty()) {
						throw new RuntimeException("Unauthorized");
					}

					String jwt = extractToken(tokenList.get(0));
					String username = jwtUtils.getUserNameFromJwtToken(jwt);

					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

					SecurityContextHolder.getContext().setAuthentication(authentication);
					log.info("Authorizing websocket: {}", username);
					accessor.setUser(authentication);
				}
				return message;
			}
		});
	}

	private String extractToken(String header) {
		return header.substring(7, header.length());
	}
}
