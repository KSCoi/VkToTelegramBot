package ru.tsoyk.service;

import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tsoyk.config.BotConfiguration;
import ru.tsoyk.config.VkConfig;

import java.util.List;
import java.util.Locale;

@Component
public class Bot extends TelegramLongPollingBot {

    private final BotConfiguration configuration;

    private final VkConfig vkConfig;

    public Bot(BotConfiguration configuration, VkConfig vkConfig)  {
        this.configuration = configuration;
        this.vkConfig = vkConfig;
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
            answerMessage.setText("Hello"); }

        else if(text.contains("/new"))   {
            try {
              VkConfig.ts = vkConfig.getVkApi().messages().getLongPollServer(vkConfig.getActor()).execute().getTs();
                System.out.println(VkConfig.ts + " THIS IS TS");
                answerMessage.setText("?");
                MessagesGetLongPollHistoryQuery eventsQuery = vkConfig.getVkApi().messages()
                        .getLongPollHistory(vkConfig.getActor()).ts(VkConfig.ts);
                System.out.println(eventsQuery.execute().getMessages().getCount() + "COUNT");

               // System.out.println(vkConfig.getVkApi().messages().getById(vkConfig.getActor(),0).execute().getItems().get(0).toString());

                Vk vk = new Vk(vkConfig);
                vk.some();
                List var = vk.getUsersNameFromGroup(vkConfig.getVkGroupId().toString());
            }
            catch (Exception e)   {

            }
        }
        else    {
            answerMessage.setText("Unknown command");
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
