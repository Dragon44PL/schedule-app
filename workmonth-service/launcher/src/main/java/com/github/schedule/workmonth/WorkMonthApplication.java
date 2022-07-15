package com.github.schedule.workmonth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class WorkMonthApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkMonthApplication.class, args);
    }
}
