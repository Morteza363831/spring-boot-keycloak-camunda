package org.camunda.bpm.getstarted.springbootcamundakeycloakexample;

import com.sun.istack.Builder;
import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.ForwardedHeaderFilter;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Camunda Web application SSO configuration for usage with KeycloakIdentityProviderPlugin.
 */
@ConditionalOnMissingClass("org.springframework.test.context.junit.jupiter.SpringExtension")
@EnableWebSecurity
@Configuration
public class WebAppSecurityConfig {

    @Bean
    public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/camunda/api/**","/camunda/engine-rest/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/camunda/assets/**","/camunda/app/**","/camunda/api/**","/camunda/lib/**")
                        .authenticated()
                        .anyRequest()
                        .permitAll())
                .authenticationProvider(keycloakAuthenticationProvider())  // Ensure this line is uncommented
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint.oidcUserService(this.oidcUserService())
                                )
                                .defaultSuccessUrl("/camunda/app/welcome/default/#!/welcome", true)
                ).build();
    }


    @Bean
    public CustomOidcUserService oidcUserService() {
        return new CustomOidcUserService();
    }

    @Bean
    public KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        return new KeycloakAuthenticationProvider();
    }

    @Bean
    public FilterRegistrationBean<ContainerBasedAuthenticationFilter> containerBasedAuthenticationFilter() {
        FilterRegistrationBean<ContainerBasedAuthenticationFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new ContainerBasedAuthenticationFilter());
        filterRegistration.setInitParameters(Collections.singletonMap("authentication-provider", "org.camunda.bpm.getstarted.springbootcamundakeycloakexample.KeycloakAuthenticationProvider"));
        filterRegistration.setOrder(201); // Ensure the filter is registered after the Spring Security Filter Chain
        filterRegistration.addUrlPatterns("/camunda/app/*");
        return filterRegistration;
    }

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }


    @Bean
    @Order(0)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
}
