package org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;

public interface UserService {

    void processUser(ProcessEngine engine , OidcUser oidcUser);

    void addUser(OidcUser oidcUser, ProcessEngine engine);

    List<GrantedAuthority> getUserAuthorities(AuthenticationResult authenticationResult);
}
