package com.haoran.Brainstorming.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class ServerRunner implements CommandLineRunner {

    private Logger log = LoggerFactory.getLogger(ServerRunner.class);

    @Override
    public void run(String... args) {
    }
}
