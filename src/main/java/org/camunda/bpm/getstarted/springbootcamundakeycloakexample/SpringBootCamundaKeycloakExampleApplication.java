package org.camunda.bpm.getstarted.springbootcamundakeycloakexample;

import lombok.extern.slf4j.Slf4j;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
@EnableProcessApplication
@Slf4j

public class SpringBootCamundaKeycloakExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCamundaKeycloakExampleApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner run(ApplicationContext appContext)
    {
        return args -> {
        String[] beans = appContext.getBeanDefinitionNames();
        Arrays.stream(beans).sorted().forEach(System.out::println);
    };
    }*/
}
