package ru.tsoyk.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tsoyk.config.BotConfiguration;

import java.util.Locale;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    private final BotConfiguration configuration;

    public Bot(BotConfiguration configuration)  {
        this.configuration = configuration;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText())    {
            sendMsg(update);
        }
    }


    public void sendMsg(Update update)   {
        SendMessage answerMessage = new SendMessage();
        String text;
        String chatId;
        if(update.getMessage() != null) {
            chatId = update.getMessage().getChatId().toString();
            answerMessage.setChatId(chatId);
            text = update.getMessage().getText().toLowerCase(Locale.ROOT);
        }
        else {
            chatId =update.getChannelPost().getChatId().toString();
            answerMessage.setChatId(chatId);
            text = update.getChannelPost().getText();
        }
        if(text.contains("/start")) {
            answerMessage.setText("Hello");
        }
        try {
            execute(answerMessage);
        }
        catch (TelegramApiException e)  {
            log.debug(e.getStackTrace().toString());
        }
    }

    @Override
    public String getBotUsername() {
        return configuration.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return configuration.getToken();
    }


}
