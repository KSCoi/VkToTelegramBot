package ru.tsoyk.service;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.users.UserMin;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollServerQuery;
import lombok.Getter;
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

    public Vk(VkConfig vkConfig) {
        this.vkConfig = vkConfig;
        try {
            VkConfig.ts  = vkConfig.getVkApi().messages()
                    .getLongPollServer(vkConfig.getActor()).execute().getTs();
        }
        catch (Exception e) {
            log.debug(e.getStackTrace().toString());
        }

    }

    public String getMessageText() throws ClientException, ApiException {
        MessagesGetLongPollHistoryQuery eventsQuery = vkConfig.getVkApi().messages()
                .getLongPollHistory(vkConfig.getActor()).ts(VkConfig.ts);
        List<Message> messages = eventsQuery
                .execute()
                .getMessages()
                .getItems();

        if (!messages.isEmpty()) {
            try {
                VkConfig.ts = vkConfig.getVkApi().messages()
                        .getLongPollServer(vkConfig.getActor())
                        .execute()
                        .getTs();

            } catch (ClientException e) {
                e.printStackTrace();

            }
        }
        if (!messages.isEmpty() && !messages.get(0).isOut()) {
            /*
            messageId - максимально полученный ID, нужен, чтобы не было ошибки 10 internal server error,
            который является ограничением в API VK. В случае, если ts слишком старый (больше суток),
            а max_msg_id не передан, метод может вернуть ошибку 10 (Internal server error).
             */
            int messageId = messages.get(0).getId();

            return messages.get(0).getText();
        }
        return null;

    }

    public void some() throws ClientException, ApiException {
        //vkConfig.getVkApi().groupsLongPoll().getMembers(vkConfig.getActor()).execute().toString();
       // System.out.println(vkConfig.getVkApi().groupsLongPoll().getMembers(vkConfig.getActor()).execute().toString()
        //+ "SOME WTF");
        //;
        System.out.println(vkConfig.getVkApi().groups().getLongPollSettings(vkConfig.getActor(), vkConfig.getVkGroupId()).executeAsString());
    }

    public ArrayList<String> getUsersNameFromGroup(String groupId) throws ClientException, ApiException {
        ArrayList<String > nameList= new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        idList = vkConfig.getVkApi().groups().getMembers(vkConfig.getActor()).groupId(groupId).execute().getItems();
        System.out.println(vkConfig.getVkApi().users().get(vkConfig.getActor()).userIds(idList.get(0).toString())
                .fields(Fields.FIRST_NAME_NOM, Fields.LAST_NAME_NOM).executeAsString());

        return nameList;
    }
}
