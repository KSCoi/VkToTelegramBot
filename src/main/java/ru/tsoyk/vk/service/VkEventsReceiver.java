package ru.tsoyk.vk.service;

import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.queries.longpoll.GetLongPollEventsQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tsoyk.tg.service.EventTypes;
import ru.tsoyk.tg.service.TgUpdateReceiver;
import ru.tsoyk.vk.config.VkConfig;

import java.util.ArrayList;
import java.util.List;


@Component
@Getter
@Setter
@Slf4j
public class VkEventsReceiver {

    private final
    VkConfig vkConfig;

    private String server;
    private String key;

    @Autowired
    private TgUpdateReceiver tgUpdateReceiver;
    @Autowired
    private VkEventsParserFactory eventsParserFactory;

    public VkEventsReceiver(VkConfig vkConfig) {
        this.vkConfig = vkConfig;
        try {
            VkConfig.ts = Integer.valueOf(vkConfig.getVkApi().groups()
                    .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getTs());
            server = vkConfig.getVkApi().groups()
                    .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getServer();
            key = vkConfig.getVkApi().groups()
                    .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getKey();
        } catch (ClientException | ApiException exception) {
            log.error("api exception", exception);
        }
    }

    public List<String> getMessageText(List<EventTypes> eventTypesList) throws ClientException, ApiException {
        if(eventTypesList==null || eventTypesList.isEmpty())    {
            eventTypesList = tgUpdateReceiver.getAllEvents();
        }
        List<String> arrayList = new ArrayList<>();
        GetLongPollEventsQuery eventsQuery = vkConfig.getVkApi().longPoll()
                .getEvents(server, key, VkConfig.ts);

        try {
            for (JsonObject j : eventsQuery.execute().getUpdates()) {
                EventTypes currentEvent = getEventFromJson(j);
                if(!currentEvent.equals(EventTypes.UNKNOWN_EVENT) && eventTypesList.contains(currentEvent))   {
                    arrayList.add(eventsParserFactory.getVkParser(currentEvent).parse(j));
                }
                else if (currentEvent.equals(EventTypes.UNKNOWN_EVENT)) {
                    log.warn("UNKNOWN EVENT",j.toString());
                }
            }
        } catch (ApiException exception) {
            key = vkConfig.getVkApi().groups()
                    .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId()).execute().getKey();
            log.error("vk api exception", exception);
        }

        if (!arrayList.isEmpty()) {
            try {
                VkConfig.ts = Integer.valueOf(vkConfig.getVkApi().groups()
                        .getLongPollServer(vkConfig.getActor(), vkConfig.getVkGroupId())
                        .execute()
                        .getTs());

            } catch (ClientException exception) {
                log.error("vk api exception", exception);
            }

        }
        /*if (!arrayList.isEmpty() && (!arrayList.get(0).isEmpty()|| arrayList.get(0).isBlank())) {
            return arrayList;
        }*/
        return arrayList;
    }

    private EventTypes getEventFromJson(JsonObject j) {
        String type = j.getAsJsonPrimitive("type").toString();
        EventTypes currentEvent;
        if(type.equals("\"message_new\""))
            currentEvent =  EventTypes.MESSAGE_NEW;
//        else if(type.contains("delete"))
//            currentEvent = EventTypes.DELETED_EVENT;
        else if (type.equals("\"message_reply\""))
            currentEvent = EventTypes.MESSAGE_REPLY;
        else if(type.equals("\"wall_post_new\""))
            currentEvent = EventTypes.WALL_POST_NEW;
        else if(type.contains("wall_reply"))
            currentEvent = EventTypes.WALL_REPLY;
        else if (type.contains("photo_comment"))
            currentEvent = EventTypes.PHOTO_COMMENT_NEW;
        else if(type.contains("video_comment"))
            currentEvent = EventTypes.VIDEO_COMMENT_NEW;
        else if(type.contains("board_post"))
            currentEvent = EventTypes.BOARD_POST_NEW;
        else
            currentEvent = EventTypes.UNKNOWN_EVENT;
        return currentEvent;
    }
}
