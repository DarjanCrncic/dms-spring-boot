package com.example.dms.security.configuration.acl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionFactory;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
@Log4j2
public class AclContext {

	@Autowired
    DataSource dataSource;
	
	@Autowired
	private Environment environment;

    @Bean 
    public JdbcMutableAclService aclService() { 
    	JdbcMutableAclService jdbcMutableAclService = new JdbcMutableAclService(
    			dataSource, lookupStrategy(), aclCache()); 
    	
    	Stream<String> stream = Arrays.stream(environment.getActiveProfiles());
    	log.debug("active profile: {}", Arrays.toString(environment.getActiveProfiles()));
    	if (stream.anyMatch((profile) -> profile.equalsIgnoreCase("mysql"))) {
    		log.debug("setting @IDENTITY");
    		jdbcMutableAclService.setClassIdentityQuery("SELECT @@IDENTITY");
    		jdbcMutableAclService.setSidIdentityQuery("SELECT @@IDENTITY");
    	}
    	
    	jdbcMutableAclService.setAclClassIdSupported(true);
    	return jdbcMutableAclService;
    }
    
    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(
          new SimpleGrantedAuthority("ROLE_ADMIN")); // ROLE_ADMIN alsways has the permission to modify acl's
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(
          new ConsoleAuditLogger());
    }

    @Bean
    public AclCache aclCache() {
        return new SpringCacheBasedAclCache(
                new ConcurrentMapCache("aclCache"),
                permissionGrantingStrategy(),
                aclAuthorizationStrategy()
        );
    }
    
    @Bean
    public PermissionEvaluator permissionEvaluator() {
		AclPermissionEvaluator permissionEvaluator = new DmsAclPermissionEvaluator(aclService());
		permissionEvaluator.setPermissionFactory(permissionFactory());
		return permissionEvaluator;
    }

    @Bean 
    public LookupStrategy lookupStrategy() { 
        BasicLookupStrategy basicLookupStrategy = new BasicLookupStrategy(
          dataSource, 
          aclCache(), 
          aclAuthorizationStrategy(), 
          new ConsoleAuditLogger()
        ); 
        basicLookupStrategy.setAclClassIdSupported(true);
		basicLookupStrategy.setPermissionFactory(permissionFactory());
        return basicLookupStrategy;
    }

	@Bean
	public PermissionFactory permissionFactory() {
		return new DefaultPermissionFactory(CustomBasePermission.class);
	}
    
    @Bean
    public MethodSecurityExpressionHandler 
      defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator());
        return expressionHandler;
    }
}
