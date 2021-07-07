package ru.tsoyk.tg.service;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.tsoyk.tg.handlers.BotMessageHandler;
import ru.tsoyk.tg.models.EventTypes;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// PartialBotApiMethod<? extends Serializable>  для всех видов сообщений:AnswerMessage,BotApiMethod,
@Component
@Getter
public class TgUpdateReceiver {
    private final BotMessageHandler messageHandler;
    private final ReplyMessageService replyMessageService;
    private final BotUserService botUserService;

    final List<EventTypes> allEvents = Arrays.asList(EventTypes.values());
    private List<EventTypes> currentEvents = new ArrayList<>();

    public TgUpdateReceiver(BotMessageHandler messageHandler, ReplyMessageService replyMessageService,
                            BotUserService botUserService) {
        this.messageHandler = messageHandler;
        this.replyMessageService = replyMessageService;
        this.botUserService = botUserService;
    }

    public PartialBotApiMethod<? extends Serializable> handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            EventTypes eventType = getEventTypes(message);
            if (eventType == null)
                eventType = EventTypes.UNKNOWN_EVENT;
            return messageHandler.handle(message, eventType);
        } else {
            return replyMessageService.sendTextMessage(update.getMessage().getChatId(), "I know only text message");
        }

    }

    private EventTypes getEventTypes(Message message) {
        String userText = message.getText();
        EventTypes eventTypes;
        switch (userText) {
            case ("1"):
                eventTypes = EventTypes.MESSAGE_NEW;
                break;
            case ("2"):
                eventTypes = EventTypes.WALL_REPLY;
                break;
            case ("3"):
                eventTypes = EventTypes.MESSAGE_REPLY;
                break;
            case ("4"):
                eventTypes = EventTypes.WALL_POST_NEW;
                break;
            case ("5"):
                eventTypes = EventTypes.BOARD_POST_NEW;
                break;
            case ("6"):
                eventTypes = EventTypes.PHOTO_COMMENT_NEW;
                break;
            case ("7"):
                eventTypes = EventTypes.VIDEO_COMMENT_NEW;
                break;
            default:
                eventTypes = EventTypes.UNKNOWN_EVENT;
                break;

        }
        return eventTypes;

    }
}
