package com.example.cryptobot.bot.command;

import com.example.cryptobot.repository.SubscriberRepository;
import com.example.cryptobot.model.Subscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {
    @Autowired
    private final SubscriberRepository subscriberRepository;


    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        Long userId = message.getChatId();

        if (subscriberRepository.existsByUserId(userId)) {
            Subscriber subscriber = subscriberRepository.findByUserId(userId);
            String text = "Вы подписаны на стоимость биткоина ";
            text = subscriber.getPriceSubscribedOn() != null ?
                    text + subscriber.getPriceSubscribedOn() + " USD" : "Активные подписки отсутствуют";
            answer.setText(text);
            try {
                absSender.execute(answer);
            } catch (TelegramApiException e) {
                log.error("Error occurred in /get_subscription command", e);

            }
        }
    }
}