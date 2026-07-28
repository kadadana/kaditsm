package com.kadadana.kaditsm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication 
@EnableScheduling
public class KadITSMApplication {
    public static void main(String[] args) {
        SpringApplication.run(KadITSMApplication.class, args);
    }
}