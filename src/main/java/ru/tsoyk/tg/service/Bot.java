package ru.tsoyk.tg.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tsoyk.vk.service.VkEventsReceiver;
import ru.tsoyk.tg.config.BotConfiguration;
import ru.tsoyk.vk.config.VkConfig;

import java.util.ArrayList;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final BotConfiguration configuration;

    private static String chatId;
    private final VkEventsReceiver vkEventsReceiver;

    private final VkConfig vkConfig;
    private final TgUpdateReceiver updateReceiver;
    private ArrayList<EventTypes> eventTypes;

    public Bot(BotConfiguration configuration, VkEventsReceiver vkEventsReceiver, VkConfig vkConfig, TgUpdateReceiver updateReceiver) {
        this.configuration = configuration;
        this.vkEventsReceiver = vkEventsReceiver;
        this.vkConfig = vkConfig;
        this.updateReceiver = updateReceiver;
    }

    @Override
    //@EventListener({ContextRefreshedEvent.class})
    public void onUpdateReceived(Update update) {
        try {
            //updateReceiver.handleUpdate(update);
            execute((SendMessage) updateReceiver.handleUpdate(update));
        } catch (TelegramApiException ex) {
            try {
                log.warn("Something wrong with execute of tg update", ex);
                vkEventsReceiver.setKey(vkEventsReceiver.getVkConfig().getVkApi().groups()
                        .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getKey());
            } catch (ClientException | ApiException apiException) {
                log.error("error in tg api", apiException);
            }
        }
    }

    public void sendMsgToChannelWithoutUpdate(String chatId) throws ClientException, ApiException {
        SendMessage answerMessage = new SendMessage();
        if (chatId != null) {
            answerMessage.setChatId(chatId);
            for (String s : vkEventsReceiver.getMessageText(updateReceiver.getCurrentEvents())) {
                answerMessage.setText(s + "\n");
                try {
                    if (!answerMessage.getText().isEmpty() && !answerMessage.getText().isBlank())   {
                        execute(answerMessage);
                    }
                } catch (TelegramApiException exception) {
                    log.error("Api exception", exception);
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
