package ru.tsoyk.service;

import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.queries.longpoll.GetLongPollEventsQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tsoyk.config.VkConfig;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class Vk {

    final
    VkConfig vkConfig;

    static String server;
    static String key;

    @Autowired
    VkMessageParser vkMessageParser;

    public Vk(VkConfig vkConfig) {
        this.vkConfig = vkConfig;
        try {
            VkConfig.ts  = Integer.valueOf(vkConfig.getVkApi().groups()
                    .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getTs());
            server = vkConfig.getVkApi().groups()
                    .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getServer();
            key = vkConfig.getVkApi().groups()
                    .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getKey();
        }
        catch (Exception e) {
            for(StackTraceElement el:e.getStackTrace()) {
                log.info(el.toString());
            }
        }

    }

    public List<String> getMessageText() throws ClientException, ApiException {
        List<String> arrayList = new ArrayList<>();
        GetLongPollEventsQuery eventsQuery = vkConfig.getVkApi().longPoll()
                .getEvents(server,key,VkConfig.ts);

        try {
            for(JsonObject j : eventsQuery.execute().getUpdates()) {
            arrayList.add(vkMessageParser.eventJsonParser(j).toString());
            }
        }
        catch (ApiException exception)  {
            key = vkConfig.getVkApi().groups()
                    .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getKey();
            for(StackTraceElement e:exception.getStackTrace())  {
                log.info(e.toString());
            };
        }

        if (!arrayList.isEmpty()) {
            try {
                VkConfig.ts = Integer.valueOf(vkConfig.getVkApi().groups()
                        .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId())
                        .execute()
                        .getTs());

            } catch (ClientException exception) {
                for(StackTraceElement element:exception.getStackTrace())  {
                    log.info(element.toString());
                };

            }
        }
        if (!arrayList.isEmpty() && (!arrayList.get(0).isEmpty()|| arrayList.get(0).isBlank())) {
            return arrayList;
        }
        return null;

    }
}
