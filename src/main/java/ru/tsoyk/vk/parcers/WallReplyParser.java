package ru.tsoyk.vk.parcers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.wall.WallComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import ru.tsoyk.vk.config.VkConfig;

@Service
@Configurable //не помогает видимо без @EnableSpringConfigured в APP
public class WallReplyParser implements VkParserInterface {

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
        String type = json.getAsJsonPrimitive("type").toString();
        if (type.equals("\"wall_reply_delete\"")) {
            User user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                    .userIds(json.getAsJsonObject("object")
                            .getAsJsonPrimitive("deleter_id").toString())
                    .execute().get(0);
            s.append("Пользователь: \"").append(user.getFirstName()).append(" ")
                    .append(user.getLastName()).append("\" удалил комментарий под постом: \n")
                    .append(getDeletedPostCommentUrl(json))
                    .append("\n");
            return s.toString();
        }
        WallComment wallComment = gson.fromJson(json.getAsJsonObject("object"), WallComment.class);
        User user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                .userIds(wallComment.getFromId().toString())
                .execute().get(0);
        switch (type) {
            case "\"wall_reply_new\"":
                s.append("Новый комментарий под постом: ").append(getPostCommentUrl(wallComment));
                s.append("Имя комментатора: \"").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\"\n");
                s.append("Текст комментария: \"").append(wallComment.getText()).append("\"\n");
                break;
            case ("\"wall_reply_edit\""):
                s.append("Пользователь: \"").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\" отредактировал комментарий под постом: ")
                        .append("\n").append(getPostCommentUrl(wallComment))
                        .append("Текст отредактированного комментария: \"")
                        .append(wallComment.getText()).append("\"\n");
                break;
            case ("\"wall_reply_restore\""):
                s.append("Пользователь: \"").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\" восстановил коментарий под постом: ")
                        .append("\n").append(getPostCommentUrl(wallComment))
                        .append("Текст комментария:\" ").append(wallComment.getText()).append("\"\n");
                break;
        }
        if (wallComment.getAttachments() != null) {
            s.append(attachmentsParser.parse(json));
        }
        return s.toString();
    }

    public String getPostCommentUrl(WallComment wallComment) {
        return "https://vk.com/public" + vkConfig.getVkGroupId() + "?w=wall" + wallComment.getOwnerId()
                + "_" + wallComment.getPostId() + "_r" + wallComment.getId() + "\n";
    }

    public String getDeletedPostCommentUrl(JsonObject jsonObject) {
        return "https://vk.com/public" + vkConfig.getVkGroupId() + "?w=wall" +
                jsonObject.getAsJsonObject("object").getAsJsonPrimitive("owner_id")
                + "_" + jsonObject.getAsJsonObject("object").getAsJsonPrimitive("post_id") + "\n";
    }
}
