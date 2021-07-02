package ru.tsoyk.tg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class BotMessageHandler implements Handler {
    private TgUpdateReceiver tgUpdateReceiver;
    private final ReplyMessageService replyMessageService;

    @Autowired
    @Lazy
    public void setTgUpdateReceiver(TgUpdateReceiver tgUpdateReceiver) {
        this.tgUpdateReceiver = tgUpdateReceiver;
    }

    public BotMessageHandler(ReplyMessageService replyMessageService) {
        this.replyMessageService = replyMessageService;
    }

    @Override
    public BotApiMethod<Message> handle(Message message, EventTypes eventType) {
        Long chatId = message.getChatId();
        if (eventType.equals(EventTypes.BOARD_POST_NEW)) {
            tgUpdateReceiver.getCurrentEvents().add(EventTypes.BOARD_POST_NEW);
            return replyMessageService.sendTextMessage(chatId, "You add BOARD events");
        } else if (eventType.equals(EventTypes.MESSAGE_NEW)) {
            tgUpdateReceiver.getCurrentEvents().add(EventTypes.MESSAGE_NEW);
            return replyMessageService.sendTextMessage(chatId, "You add personal message events");
        } else if (eventType.equals(EventTypes.MESSAGE_REPLY)) {
            tgUpdateReceiver.getCurrentEvents().add(EventTypes.MESSAGE_REPLY);
            return replyMessageService.sendTextMessage(chatId, "You add MESSAGE REPLY events");
        } else if (eventType.equals(EventTypes.WALL_POST_NEW)) {
            tgUpdateReceiver.getCurrentEvents().add(EventTypes.WALL_POST_NEW);
            return replyMessageService.sendTextMessage(chatId, "You add WALL POST events");
        } else if (eventType.equals(EventTypes.WALL_REPLY)) {
            tgUpdateReceiver.getCurrentEvents().add(EventTypes.WALL_REPLY);
            return replyMessageService.sendTextMessage(chatId, "You add WALL REPLY events");
        }
        return replyMessageService.sendTextMessage(chatId, "Hi");
    }
}
