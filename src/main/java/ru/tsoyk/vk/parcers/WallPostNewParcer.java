package ru.tsoyk.vk.parcers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.wall.PostType;
import com.vk.api.sdk.objects.wall.WallpostFull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tsoyk.tg.models.EventTypes;
import ru.tsoyk.vk.config.VkConfig;

@Service
public class WallPostNewParcer implements VkParserInterface {
    @Autowired
    VkConfig vkConfig;
    @Autowired
    AttachmentsParser attachmentsParser;
    @Autowired
    DeletedEventParser deletedEventParser;

    @Override
    public String parse(JsonObject json, EventTypes eventType) throws ClientException, ApiException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        StringBuilder s = new StringBuilder("");
        WallpostFull wallPost = gson.fromJson(json.getAsJsonObject("object"), WallpostFull.class);
        User user;
        Group group;
        if (isEventFromGroup(json, eventType)) {
            group = connectToGroup(json, eventType, vkConfig);
            user = new User();
            user.setFirstName(group.getName());
            user.setLastName(group.getType().toString());
        } else
            user = connectToUser(json, eventType, vkConfig);

        if (wallPost.getPostType().equals(PostType.SUGGEST)) {
            s.append("Новый пост в предложке от пользователя").append("\"\n")
                    .append(user.getFirstName()).append(" ").append(user.getLastName()).append("\"\n")
                    .append("По ссылке: ").append(getWallPostUrl(wallPost)).append("\n")
                    .append("Текст поста:\" ").append(wallPost.getText()).append("\"\n");
            return s.toString();
        }
        if (wallPost.getAttachments() != null) {
            s.append(attachmentsParser.parse(json, eventType));
        }
        return s.toString();
    }

    public String getWallPostUrl(WallpostFull wallPost) {
        return "https://vk.com/public" + vkConfig.getVkGroupId() + "?w=wall" + wallPost.getOwnerId()
                + "_" + wallPost.getId();
    }
}
