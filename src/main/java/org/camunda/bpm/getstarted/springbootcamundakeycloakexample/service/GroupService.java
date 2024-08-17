package org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface GroupService {

    void processGroups(ProcessEngine engine, OidcUser oidcUser);




}
