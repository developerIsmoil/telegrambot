package com;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyBotApplication {
    @Autowired
    private TelegramBot telegramBot;

    public static void main(String[] args) {
        SpringApplication.run(MyBotApplication.class, args);
    }

}
