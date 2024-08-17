package org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
public class GroupServiceImpl implements GroupService {

    @Override
    public void processGroups(ProcessEngine engine, OidcUser oidcUser) {
        IdentityService identityService = engine.getIdentityService();
        if (identityService.createGroupQuery().groupId("camunda-admin").count() == 0) {
            identityService.saveGroup(identityService.newGroup("camunda-admin"));
        }
        if (identityService.createGroupQuery().groupId("camunda-admin").groupMember(oidcUser.getName()).count() == 0) {
            identityService.createMembership(oidcUser.getName(), "camunda-admin");
        }
    }
}
