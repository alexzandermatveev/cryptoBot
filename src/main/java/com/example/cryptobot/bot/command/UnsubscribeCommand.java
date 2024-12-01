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

/**
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class UnsubscribeCommand implements IBotCommand {

    private final SubscriberRepository subscriberRepository;


    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        Long userId = message.getChatId();
        if (subscriberRepository.existsByUserId(userId)) {
            Subscriber subscriber = subscriberRepository.findByUserId(userId);
            subscriber.setPriceSubscribedOn(null);
            subscriberRepository.save(subscriber);
            answer.setText("Подписка отменена");
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                log.error("Error occurred in /unsubscribe command", e);
            }
        }
    }
}