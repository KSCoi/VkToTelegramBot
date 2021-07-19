package ru.tsoyk.tg.handlers;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tsoyk.tg.models.EventTypes;

public interface Handler {
    BotApiMethod<Message> handle(Message message, EventTypes eventType);
}
