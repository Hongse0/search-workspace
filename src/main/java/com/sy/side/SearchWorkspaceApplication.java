package com.sy.side;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SearchWorkspaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchWorkspaceApplication.class, args);
    }

}
