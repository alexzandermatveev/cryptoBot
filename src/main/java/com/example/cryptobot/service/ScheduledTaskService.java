package com.example.cryptobot.service;


import com.example.cryptobot.repository.SubscriberRepository;
import com.example.cryptobot.utils.TextUtil;
import com.example.cryptobot.bot.CryptoBot;
import com.example.cryptobot.model.Subscriber;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledTaskService {
    private final SubscriberRepository subscriberRepository;
    private final CryptoCurrencyService cryptoCurrencyService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Value("${telegram.bot.notify.delay.value}")
    private int notifyDelay;
    @Value("${telegram.bot.notify.delay.unit}")
    private String delayUnit;

    @Value("${telegram.bot.notify.frequency.value}")
    private int notifyFrequency;
    @Value("${telegram.bot.notify.frequency.unit}")
    private String frequencyUnit;

    @SneakyThrows
    public void startScheduledTask(CryptoBot bot) {
        TimeUnit timeUnit = getTimeUnit(frequencyUnit);
//        запуск задачи с фиксированным интервалом
        scheduler.scheduleAtFixedRate(() -> checkPrice(bot), 0, notifyFrequency, timeUnit);
    }

    public void checkPrice(CryptoBot bot) {
        double price;
        try {
            price = cryptoCurrencyService.getBitcoinPrice();
        } catch (IOException e) {
            throw new RuntimeException("service with bitcoin price not available");
        }
        List<Subscriber> subscribers = subscriberRepository.findAllByPriceSubscribedOnLessThanEqual(price);
        List<Subscriber> updatedSubscribers = new ArrayList<>();

        SendMessage message = new SendMessage();

        message.setText("Пора покупать, стоимость биткоина " + TextUtil.toString(price));
        subscribers.forEach(subscriber -> {
            if (subscriber.getLastNotified() == null ||
                    Instant.now().isAfter(subscriber
                            .getLastNotified()
                            .plusMillis(getTimeUnit(delayUnit).toMillis(notifyDelay)))) {
                message.setChatId(subscriber.getUserId());
                subscriber.setLastNotified(Instant.now());
                updatedSubscribers.add(subscriber);
                try {
                    bot.execute(message);
                } catch (TelegramApiException e) {
                    log.error("Error occurred in checkingPrice command", e);
                }
            }
        });
        subscriberRepository.saveAll(updatedSubscribers);
    }

    private TimeUnit getTimeUnit(String timeUnit) {
        return switch (timeUnit.toUpperCase()) {
            case "SECONDS" -> TimeUnit.SECONDS;
            case "MINUTES" -> TimeUnit.MINUTES;
            case "HOURS" -> TimeUnit.HOURS;
            default -> throw new IllegalArgumentException("Unsupported TimeUnit: " + timeUnit);
        };
    }
}
