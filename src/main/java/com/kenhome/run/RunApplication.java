package com.kenhome.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com"})
@EnableAsync
@EnableScheduling
public class RunApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunApplication.class, args);
    }
}
