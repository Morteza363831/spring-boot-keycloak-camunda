package org.camunda.bpm.getstarted.springbootcamundakeycloakexample;



import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.camunda.bpm.extension.keycloak.plugin.KeycloakIdentityProviderPlugin;
@Component
@ConfigurationProperties(prefix = "plugin.identity.keycloak")
public class KeycloakIdentityProvider extends KeycloakIdentityProviderPlugin {
    // Add necessary configuration properties here
}
