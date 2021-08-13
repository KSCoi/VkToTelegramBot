package ru.tsoyk.tg.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.tsoyk.tg.models.BotUser;
import ru.tsoyk.tg.models.EventTypes;
import ru.tsoyk.tg.models.Role;
import ru.tsoyk.tg.service.BotUserService;
import ru.tsoyk.tg.service.ReplyMessageService;
import ru.tsoyk.tg.service.TgUpdateReceiver;

@Component
public class BotMessageHandler implements Handler {
    private TgUpdateReceiver tgUpdateReceiver;
    private final ReplyMessageService replyMessageService;
    private final BotUserService botUserService;

    @Autowired
    @Lazy
    public void setTgUpdateReceiver(TgUpdateReceiver tgUpdateReceiver) {
        this.tgUpdateReceiver = tgUpdateReceiver;
    }

    public BotMessageHandler(ReplyMessageService replyMessageService, BotUserService botUserService) {
        this.replyMessageService = replyMessageService;
        this.botUserService = botUserService;
    }

    @Override
    public BotApiMethod<Message> handle(Message message, EventTypes eventType) {
        Long chatId = message.getChatId();
        if (message.getText().contains("/register")) {
            String password = message.getText().replace("/register ", "");
            BotUser user = botUserService.getUserByChatId(chatId);
            if (user != null) {
                return replyMessageService.sendTextMessage(chatId, "Вы уже зарегистрированы");
            } else {
                user = new BotUser();
                user.setPassword(password);
                user.setId(chatId);
                botUserService.save(user);
                return replyMessageService.sendTextMessage(chatId, "регистрация успешно завершена");
            }
        } else if (message.getText().equals("/help") || message.getText().equals("/start")) {
            return replyMessageService.sendTextMessage(chatId,
                    "Вы можете конфигурировать события, которые будет отслеживать бот " +
                            "и отсылать их вам в канал следующими командами: \n" +
                            "\"1\" : события в личных сообщений вашей группы\n" +
                            "\"2\" : события коментариев под постами группы \n" +
                            "\"3\" : события личных сообщений, который были отправлены от лица вашей группы\n" +
                            "\"4\" : события новых постов, например в предложке вашей группы\n" +
                            "\"5\" : события в обсуждениях вашей группы\n" +
                            "\"6\" : события под фото в вашей группе\n" +
                            "\"7\" : события под видео в вашей группе.\n" +
                            "Для конфигурирвания вам необходимо зарегистрироваться командой /register и ввести пароль через пробел \n" +
                            "Например: /register 1234567 \n" + "А также иметь доступ к конфигурированию, который можно получить написав автору бота в " +
                            "telegram по ссылке: https://t.me/ksTsoy");
        }
        if (botUserService.getUserByChatId(chatId) != null) {
            if (botUserService.getUserByChatId(message.getChatId()).getRole().equals(Role.USER))
                return replyMessageService.sendTextMessage(message.getChatId(), "Вы не можете конфигурировать получаемые в канале сообщения");
            else if (eventType.equals(EventTypes.BOARD_POST_NEW)) {
                tgUpdateReceiver.getCurrentEvents().add(EventTypes.BOARD_POST_NEW);
                return replyMessageService.sendTextMessage(chatId, "ВЫ добавили события КОММЕНТАРИИ В ОБСУЖДЕНИИ");
            } else if (eventType.equals(EventTypes.MESSAGE_NEW)) {
                tgUpdateReceiver.getCurrentEvents().add(EventTypes.MESSAGE_NEW);
                return replyMessageService.sendTextMessage(chatId, "Вы добавили событие НОВЫЕ ЛИЧНЫЕ СООБЩЕНИЯ");
            } else if (eventType.equals(EventTypes.MESSAGE_REPLY)) {
                tgUpdateReceiver.getCurrentEvents().add(EventTypes.MESSAGE_REPLY);
                return replyMessageService.sendTextMessage(chatId, "Вы добавили событие ЛИЧНЫЕ СООБЩЕНИЯ ОТ ИМЕНИ ГРУППЫ");
            } else if (eventType.equals(EventTypes.WALL_POST_NEW)) {
                tgUpdateReceiver.getCurrentEvents().add(EventTypes.WALL_POST_NEW);
                return replyMessageService.sendTextMessage(chatId, "Вы добавли события ПОСТЫ В ПРЕДЛОЖКЕ");
            } else if (eventType.equals(EventTypes.WALL_REPLY)) {
                tgUpdateReceiver.getCurrentEvents().add(EventTypes.WALL_REPLY);
                return replyMessageService.sendTextMessage(chatId, "Вы добавили событие КОММЕНТАРИИ ПОД ПОСТОМ");
            } else if (eventType.equals(EventTypes.PHOTO_COMMENT_NEW))
                return replyMessageService.sendTextMessage(chatId, "Вы добавили событие КОММЕНТАРИИ ПОД ФОТО");
            else if (eventType.equals(EventTypes.VIDEO_COMMENT_NEW))
                return replyMessageService.sendTextMessage(chatId, "Вы добавили событие КОММЕНТАРИИ ПОД ВИДЕО");
        }
        return replyMessageService.sendTextMessage(chatId, "напишите команду /help");
    }
}
