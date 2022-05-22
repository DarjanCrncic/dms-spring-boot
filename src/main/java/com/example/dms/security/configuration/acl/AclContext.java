package com.example.dms.security.configuration.acl;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.extern.log4j.Log4j2;

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
    	return new DmsAclPermissionEvaluator(aclService());
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
        return basicLookupStrategy;
    }
    
    @Bean
    public MethodSecurityExpressionHandler 
      defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator());
        return expressionHandler;
    }
}
