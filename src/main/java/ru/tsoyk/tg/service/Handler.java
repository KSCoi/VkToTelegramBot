package ru.tsoyk.tg.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface Handler {
    BotApiMethod<Message> handle(Message message, EventTypes eventType);
}
