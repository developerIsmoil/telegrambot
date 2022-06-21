package com.telegram;

import com.entity.Order;
import com.entity.Product;
import com.entity.User;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.repository.OrderRepository;
import com.repository.ProductRepository;
import com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TelegramService {
    @Lazy
    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void receive(List<Update> list) {
        for (Update update : list) {
            if (update.callbackQuery() != null) {
                receiceCallBack(update);
            } else if (update.message() != null) {
                receiveMessage(update);
            }
        }
    }

    private void receiveMessage(Update update) {
        System.out.println(update.message().text());
        String message = update.message().text();
        Long userId = update.message().from().id();
        switch (message) {
            case "/start" -> {
                telegramBot.execute(new SendMessage(userId,
                        String.format("Assalomu alaykum %s, Hush kelibsiz.",
                                update.message().from().firstName())));
                User user = userRepository.getByUserId(userId).orElse(new User(
                        update.message().from().firstName(),
                        update.message().from().lastName(),
                        update.message().from().username(),
                        update.message().from().id()
                ));
                if (user.getId() == null)
                    userRepository.save(user);
            }
            case "/my_orders" -> {
                List<Order> orders = orderRepository.findByUserId(userId);
                if (orders.isEmpty()) {
                    telegramBot.execute(new SendMessage(userId,
                            "Sizning buyurtmangiz mavjud emas"));
                } else {
                    List<String> productNames = orders.stream().map(order -> {
                        return order.getProductName();
                    }).collect(Collectors.toList());
                    telegramBot.execute(new SendMessage(userId, String.format(
                            "Buyurtmalarim: %s", String.join(",", productNames))));
                }
            }
            case "/new_order" -> {
                List<InlineKeyboardButton> keys = new ArrayList<>();
                for (Product product : productRepository.findAll()) {
                    keys.add(new InlineKeyboardButton(product.getName())
                            .callbackData(String.format("new_order#%s", product.getName())));
                }
                telegramBot.execute(new SendMessage(userId, "Mahsulotni Tanlang")
                        .replyMarkup(new InlineKeyboardMarkup(
                                keys.toArray(new InlineKeyboardButton[]{})
                        )));
            }
            case "/pay" -> telegramBot.execute(new SendMessage(userId,
                    "Xaridingiz uchun raxmat"));

            case "/delete_order" -> {
                List<Order> orders = orderRepository.findByUserId(userId);
                if (orders.isEmpty()) {
                    telegramBot.execute(
                            new SendMessage(userId, "Order list empty"));
                } else {
                    orderRepository.deleteByUserId(userId);
//                    orderRepository.deleteAllById(update.message().from().id());
                    telegramBot.execute(
                            new SendMessage(userId, "Cleaned oders"));
                }
            }
            default -> telegramBot.execute(new SendMessage(userId, "/start"));
        }
    }

    private void receiceCallBack(Update update) {
        final String NEW_ORDER = "new_order#";
        Long userId = update.callbackQuery().from().id();
        CallbackQuery callback = update.callbackQuery();
        System.out.println(callback.data());
        if (callback.data().startsWith(NEW_ORDER)) {
            String productName = callback.data().split("#")[1];
            orderRepository.save(new Order(productName, userId));
        }
    }
}
