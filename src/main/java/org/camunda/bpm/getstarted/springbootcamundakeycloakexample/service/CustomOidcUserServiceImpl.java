package org.camunda.bpm.getstarted.springbootcamundakeycloakexample.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserServiceImpl extends OidcUserService implements CustomOidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        // Custom logic to handle OIDC user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            System.out.println("here");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Log user details or perform additional actions
        }

        return oidcUser;
    }
}
