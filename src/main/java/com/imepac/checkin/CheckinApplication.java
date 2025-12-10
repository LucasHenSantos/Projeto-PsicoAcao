package com.imepac.checkin;

import jakarta.annotation.PostConstruct; // Importe isto
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone; // Importe isto

@SpringBootApplication
public class CheckinApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckinApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));

        System.out.println("Fuso horário configurado para: " + TimeZone.getDefault().getID());
    }
}