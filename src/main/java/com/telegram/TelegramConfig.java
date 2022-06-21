package com.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TelegramConfig {

    @Autowired
    private TelegramService telegramService;

    @Bean
    public TelegramBot telegrambot() {
        TelegramBot telegramBot = new TelegramBot("5555218539:AAHPLqlV47yr0IG1kE7Aaxbp33JEDhpS3sE");
/*        telegramBot.setUpdatesListener(list -> {

        }); */
/*       telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> list) {
                return 0;
            }
        }); */
        telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> list) {
                try {
                    telegramService.receive(list);
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                } catch (Exception e) {
                    e.printStackTrace();
                    return UpdatesListener.CONFIRMED_UPDATES_NONE;
                }
            }
        });
        return telegramBot;
    }
}
