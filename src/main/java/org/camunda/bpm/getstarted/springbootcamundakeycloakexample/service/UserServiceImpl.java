package org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService{


    @Override
    public void processUser(ProcessEngine engine , OidcUser oidcUser) {

        IdentityService identityService = engine.getIdentityService();
        List<User> users = identityService.createUserQuery().userId(oidcUser.getName()).list();
        if (users.isEmpty()) {
            log.info("User not found in database, creating a new one.");
            addUser(oidcUser,engine);
            log.info("New user created with ID: {}", oidcUser.getName());
        } else {
            log.info("User {} found in database.", oidcUser.getName());
        }
    }

    @Override
    public void addUser(OidcUser oidcUser, ProcessEngine engine) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(oidcUser.getName());
        userEntity.setFirstName(oidcUser.getGivenName());
        userEntity.setLastName(oidcUser.getFamilyName());
        userEntity.setEmail(oidcUser.getEmail());
        engine.getIdentityService().saveUser(userEntity);
        engine.getIdentityService().getUserInfo("","");
    }

    @Override
    public List<GrantedAuthority> getUserAuthorities(AuthenticationResult authenticationResult) {
        log.info("Converting user groups to GrantedAuthority...");
        return authenticationResult.getGroups().stream()
                .map(group -> (GrantedAuthority) () -> group)
                .collect(Collectors.toList());
    }




}
