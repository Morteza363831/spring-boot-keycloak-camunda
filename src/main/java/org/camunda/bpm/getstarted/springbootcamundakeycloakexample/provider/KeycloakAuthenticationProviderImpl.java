package org.camunda.bpm.getstarted.springbootcamundakeycloakexample.provider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service.GroupService;
import org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service.GroupServiceImpl;
import org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service.UserService;
import org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class KeycloakAuthenticationProviderImpl extends ContainerBasedAuthenticationProvider implements AuthenticationProvider,KeyCloakAuthenticationProvider {

    private final UserService userService;

    private final GroupService groupService;

    public KeycloakAuthenticationProviderImpl() {
        this.userService = new UserServiceImpl();
        this.groupService = new GroupServiceImpl();
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        HttpServletRequest request = null;  // Placeholder, will need a valid HttpServletRequest
        ProcessEngine engine = null;  // Placeholder, will need a valid ProcessEngine
        AuthenticationResult authenticationResult = extractAuthenticatedUser(request, engine);
        if (!authenticationResult.isAuthenticated()) {
            log.error("Authentication failed, returning null.");
            return null;
        }
        return new UsernamePasswordAuthenticationToken(
                authenticationResult.getAuthenticatedUser(),
                null,
                userService.getUserAuthorities(authenticationResult)
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            log.error("No authentication found in SecurityContextHolder.");
            return AuthenticationResult.unsuccessful();
        }

        if (oidcUser == null || oidcUser.getName() == null || oidcUser.getName().isEmpty()) {
            log.error("OIDC user information is not present or incomplete.");
            return AuthenticationResult.unsuccessful();
        }
        userService.processUser(engine,oidcUser);
        groupService.processGroups(engine,oidcUser);
        AuthenticationResult authenticationResult = new AuthenticationResult(oidcUser.getName(), true);
        authenticationResult.setGroups(Collections.singletonList("camunda-admin"));

        return authenticationResult;
    }

    /*private List<String> getUserGroups(Authentication authentication) {
        log.info("Extracting user groups...");
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }*/

    /*private List<String> getUserGroups(String userId, ProcessEngine engine){
        List<String> groupIds = new ArrayList<>();
        // query groups using KeycloakIdentityProvider plugin
        engine.getIdentityService().createGroupQuery().groupMember(userId).list()
                .forEach( g -> groupIds.add(g.getId()));
        return groupIds;
    }*/



    /*private String getTenantForUser(String userId, ProcessEngine engine) {
        // Logic to fetch tenant for the user from ProcessEngine or any other source
        // This is just a placeholder implementation
        // Replace this with the actual logic to get the tenant
        return "";
    }*/
}
