package org.camunda.bpm.getstarted.springbootcamundakeycloakexample.config;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamundaConfig {

    @Bean
    public ProcessEngineConfigurationImpl processEngineConfiguration() {
        StandaloneInMemProcessEngineConfiguration config = new StandaloneInMemProcessEngineConfiguration();
        config.setJobExecutorActivate(true);
        // Additional configuration
        return config;
    }
}
