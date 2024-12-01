package com.example.cryptobot.bot.command;

import com.example.cryptobot.model.Subscriber;
import com.example.cryptobot.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeCommand implements IBotCommand {

    private final SubscriberRepository subscriberRepository;
    private final GetPriceCommand getPriceCommand;


    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        Long userId = message.getChatId();

        try {
            Double targetPrice = Double.parseDouble(message.getText()
                    .replaceAll("(/subscribe|\\s)", ""));
            Subscriber subscriber = subscriberRepository.existsByUserId(userId) ?
                    subscriberRepository.findByUserId(userId) : null;

            if (subscriber != null) {
                subscriber.setPriceSubscribedOn(targetPrice);
                subscriberRepository.save(subscriber);
            }
            getPriceCommand.processMessage(absSender, message, arguments);
            answer.setText("новая подписка создана на стоимость " + targetPrice + " USD");
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                log.error("Error occurred in /subscribe command", e);
            }

        } catch (NullPointerException | NumberFormatException e) {
            answer.setText("указанный аргумент не является числом\n" +
                    "попробуйте заново: /subscribe [targetPrice]");
            try {
                absSender.execute(answer);
            } catch (TelegramApiException telegramApiException) {
                log.error("Error occurred in /subscribe command", telegramApiException);
            }
            log.error("Error occurred in /subscribe command", e);
        }
    }
}