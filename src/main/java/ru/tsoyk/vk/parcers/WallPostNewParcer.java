package ru.tsoyk.vk.parcers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.wall.PostType;
import com.vk.api.sdk.objects.wall.WallpostFull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public String parse(JsonObject json) throws ClientException, ApiException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        StringBuilder s = new StringBuilder("");
        WallpostFull wallPost = gson.fromJson(json.getAsJsonObject("object"), WallpostFull.class);
        User user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                .userIds(wallPost.getFromId().toString())
                .execute().get(0);
        if (wallPost.getPostType().equals(PostType.SUGGEST)) {
            s.append("Новый пост в предложке от пользователя").append("\"\n")
                    .append(user.getFirstName()).append(" ").append(user.getLastName()).append("\"\n")
                    .append("По ссылке: ").append(getWallPostUrl(wallPost))
                    .append("Текст поста:\" ").append(wallPost.getText()).append("\"\n");
            return s.toString();
        }
        if (wallPost.getAttachments() != null) {
            s.append(attachmentsParser.parse(json));
        }
        return s.toString();
    }

    public String getWallPostUrl(WallpostFull wallPost) {
        return "https://vk.com/public" + vkConfig.getVkGroupId() + "?w=wall" + wallPost.getOwnerId()
                + "_" + wallPost.getId();
    }
}
