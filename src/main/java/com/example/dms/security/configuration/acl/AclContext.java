package com.example.dms.security.configuration.acl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
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

@Configuration
public class AclContext {

	@Autowired
    DataSource dataSource;

    @Bean 
    public JdbcMutableAclService aclService() { 
    	return new JdbcMutableAclService(
    			dataSource, lookupStrategy(), aclCache()); 
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
    public LookupStrategy lookupStrategy() { 
        return new BasicLookupStrategy(
          dataSource, 
          aclCache(), 
          aclAuthorizationStrategy(), 
          new ConsoleAuditLogger()
        ); 
    }
    
    @Bean
    public MethodSecurityExpressionHandler 
      defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}
