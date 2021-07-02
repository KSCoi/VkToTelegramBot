package ru.tsoyk.vk.parcers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tsoyk.vk.config.VkConfig;

@Service
public class NewMessageParser implements VkParserInterface {

    @Autowired
    VkConfig vkConfig;
    @Autowired
    AttachmentsParser attachmentsParser;

    @Override
    public String parse(JsonObject json) throws ClientException, ApiException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        StringBuilder s = new StringBuilder("");
        Message message = gson.fromJson(json.getAsJsonObject("object"), Message.class);
        User user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                .userIds(message.getFromId().toString())
                .execute().get(0);

        s.append("Новое личное сообщение от пользователя: ").append(user.getFirstName())
                .append(" ").append(user.getLastName()).append("\n");
        s.append("По ссылке: ").append(getDialogUrl(user));
        s.append("Текст сообщения:\" ").append(message.getText()).append("\"\n");

        if (message.getAttachments() != null) {
            s.append(attachmentsParser.parse(json));
        }
        return s.toString();
    }

    public String getDialogUrl(User user) {
        return "https://vk.com/gim" + vkConfig.getVkGroupId() + "?sel=" + user.getId() + "\n";
    }
}