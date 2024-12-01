package com.example.cryptobot.bot.command;

import com.example.cryptobot.model.Subscriber;
import com.example.cryptobot.repository.SubscriberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.UUID;


/**
 * Обработка команды начала работы с ботом
 */
@Service
@AllArgsConstructor
@Slf4j
public class StartCommand implements IBotCommand {

    private final SubscriberRepository subscriberRepository;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Запускает бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        Long userId = message.getChatId();
        answer.setChatId(message.getChatId());
        answer.setText("""
                Привет! Данный бот помогает отслеживать стоимость биткоина.
                Поддерживаемые команды:
                /get_price - получить стоимость биткоина
                /get_subscription - просмотреть подписки
                /subscribe [price] - подписаться на цену
                /unsubscribe - отписаться
                """);

        if(!subscriberRepository.existsByUserId(userId)){
            Subscriber subscriber = new Subscriber();
            subscriber.setUserId(userId);
            subscriber.setPriceSubscribedOn(null);
            subscriber.setUuid(UUID.randomUUID());
            subscriberRepository.save(subscriber);
        }


        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /start command", e);
        }
    }
}