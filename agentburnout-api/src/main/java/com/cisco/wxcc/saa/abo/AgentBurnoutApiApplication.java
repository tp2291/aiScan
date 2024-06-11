package com.cisco.wxcc.saa.abo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@EnableJpaRepositories(basePackages = "com.cisco.wxcc.saa.abo.repository")
@EnableConfigurationProperties
@EntityScan(basePackages = {"com.cisco.wxcc.saa.abo.entity"})
public class AgentBurnoutApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentBurnoutApiApplication.class, args);
    }

}
