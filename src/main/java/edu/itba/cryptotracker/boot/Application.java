package edu.itba.cryptotracker.boot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "edu.itba.cryptotracker")
@EnableJpaRepositories(basePackages = "edu.itba.cryptotracker.adapter.gateway.persistence.jpa")
@EntityScan(basePackages = "edu.itba.cryptotracker.adapter.output.persistence.jpa")
@EnableConfigurationProperties
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
