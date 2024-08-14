package org.camunda.bpm.getstarted.springbootcamundakeycloakexample;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.identity.db.DbUserQueryImpl;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class KeycloakAuthenticationProvider extends ContainerBasedAuthenticationProvider implements AuthenticationProvider {

    public static final Date ONE_HOUR_FROM_NOW;

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1); // Add one hour to the current time
        ONE_HOUR_FROM_NOW = calendar.getTime(); // Get the Date object
    }
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpServletRequest request = null;  // Placeholder, will need a valid HttpServletRequest
        ProcessEngine engine = null;  // Placeholder, will need a valid ProcessEngine

        AuthenticationResult authenticationResult = extractAuthenticatedUser(request, engine);

        if (!authenticationResult.isAuthenticated()) {
            log.error("Authentication failed, returning null.");
            return null;
        }

        log.info("Authentication successful for user: {}", authenticationResult.getAuthenticatedUser());

        return new UsernamePasswordAuthenticationToken(
                authenticationResult.getAuthenticatedUser(),
                null,
                getUserAuthorities(authenticationResult)
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication instanceof OAuth2AuthenticationToken)) {
            log.error("No authentication found in SecurityContextHolder.");
            return AuthenticationResult.unsuccessful();
        }

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        if (oidcUser == null || oidcUser.getName() == null || oidcUser.getName().isEmpty()) {
            log.error("OIDC user information is not present or incomplete.");
            return AuthenticationResult.unsuccessful();
        }

        String userId = oidcUser.getName();
        System.out.println(userId + " " + oidcUser.getEmail());
        IdentityService identityService = engine.getIdentityService();
        List<User> users = identityService.createUserQuery().userId(userId).list();



        if (users.isEmpty()) {
            log.info("User not found in database, creating a new one.");
            UserEntity userEntity = new UserEntity();
            userEntity.setId(userId);
            userEntity.setFirstName(oidcUser.getGivenName());
            userEntity.setLastName(oidcUser.getFamilyName());
            userEntity.setEmail(oidcUser.getEmail());


            engine.getIdentityService().saveUser(userEntity);
            engine.getIdentityService().getUserInfo("","");
            log.info("New user created with ID: {}", userId);
        } else {
            log.info("User {} found in database.", userId);
        }
        if (identityService.createGroupQuery().groupId("camunda-admin").count() == 0) {
            identityService.saveGroup(identityService.newGroup("camunda-admin"));
        }

        identityService.createMembership(userId, "camunda-admin");
        AuthenticationResult authenticationResult = new AuthenticationResult(userId, true);
        authenticationResult.setGroups(Collections.singletonList("camunda-admin"));

        // Fetch and set tenant
        String tenantId = getTenantForUser(userId, engine);
        if (tenantId != null) {
            authenticationResult.setTenants(Collections.singletonList(tenantId));
        }

        log.info("User {} authenticated successfully with groups {} and tenant {}", userId, authenticationResult.getGroups(), tenantId);
        return authenticationResult;
    }

    /*private List<String> getUserGroups(Authentication authentication) {
        log.info("Extracting user groups...");
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }*/

    private List<String> getUserGroups(String userId, ProcessEngine engine){
        List<String> groupIds = new ArrayList<>();
        // query groups using KeycloakIdentityProvider plugin
        engine.getIdentityService().createGroupQuery().groupMember(userId).list()
                .forEach( g -> groupIds.add(g.getId()));
        return groupIds;
    }

    private List<GrantedAuthority> getUserAuthorities(AuthenticationResult authenticationResult) {
        log.info("Converting user groups to GrantedAuthority...");
        return authenticationResult.getGroups().stream()
                .map(group -> (GrantedAuthority) () -> group)
                .collect(Collectors.toList());
    }

    private String getTenantForUser(String userId, ProcessEngine engine) {
        // Logic to fetch tenant for the user from ProcessEngine or any other source
        // This is just a placeholder implementation
        // Replace this with the actual logic to get the tenant
        return "";
    }
}
