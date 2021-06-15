package ru.tsoyk.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tsoyk.config.BotConfiguration;
import ru.tsoyk.config.VkConfig;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final BotConfiguration configuration;

    private static String chatId;
    private final Vk vk;

    private final VkConfig vkConfig;

    public Bot(BotConfiguration configuration, Vk vk,VkConfig vkConfig)  {
        this.configuration = configuration;
        this.vk = vk;
        this.vkConfig = vkConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        while (true) {
            try {
                sendMsgToChannel(update);
            }
            catch (Exception e) {
                try {
                    Vk.key = vk.vkConfig.getVkApi().groups()
                            .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getKey();
                    sendMsgToChannel(update);
                    }
                catch (ClientException | ApiException apiException )  {
                    for(StackTraceElement element : apiException.getStackTrace())   {
                        log.debug(element.toString());
                    }
                }
            }
        }
    }
    public void sendMsgToChannel(Update update) throws ClientException, ApiException {
        Message message = update.getChannelPost();
        SendMessage answerMessage = new SendMessage();
        chatId = message.getChatId().toString();
        System.out.println(chatId+ " THIS IS CHAT ID");
        answerMessage.setChatId(chatId);
            for (String s : vk.getMessageText()) {
                answerMessage.setText(s + "\n");
                try {
                    execute(answerMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
    }
    public void sendMsgToChannelWithoutUpdate(String chatId) throws ClientException, ApiException {
        SendMessage answerMessage = new SendMessage();
        if(chatId!=null)    {
            answerMessage.setChatId(chatId);
            for (String s : vk.getMessageText()) {
                answerMessage.setText(s + "\n");
                try {
                    execute(answerMessage);
                }
                catch (TelegramApiException e) {
                 e.printStackTrace();
                }
            }
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
