package ru.tsoyk.service;

import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tsoyk.config.BotConfiguration;
import ru.tsoyk.config.VkConfig;

import java.util.List;
import java.util.Locale;

@Component
public class Bot extends TelegramLongPollingBot {

    private final BotConfiguration configuration;

    //private final VkConfig vkConfig;

    private final Vk vk;

    public Bot(BotConfiguration configuration, Vk vk)  {
        this.configuration = configuration;
        this.vk = vk;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText())    {
        }

        try {
        sendMsgToChannel(update);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendMsgToChannel(Update update) throws ClientException, ApiException {
        Message message = update.getChannelPost();
        SendMessage answerMessage = new SendMessage();
        String text;
        String chatId;
        chatId = message.getChatId().toString();
        answerMessage.setChatId(chatId);
        text = message.getText().toLowerCase(Locale.ROOT);

        if(text.contains("/start")) {
            answerMessage.setText("Hello");
        }

        if(text.contains("/new"))   {
            JsonObject jsonObject = vk.getMessageText();
            answerMessage.setText(jsonObject.toString());

        }

        try {
            execute(answerMessage);
        }
        catch (TelegramApiException e)  {
            e.printStackTrace();
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
