package ru.tsoyk.vk.parcers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.wall.WallComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tsoyk.tg.models.EventTypes;
import ru.tsoyk.vk.config.VkConfig;

@Service
public class PhotoCommentParser implements VkParserInterface {

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
        String type = json.getAsJsonPrimitive("type").toString();
        System.out.println(json);
        User user;
        Group group;
        if (isEventFromGroup(json, eventType)) {
            group = connectToGroup(json, eventType, vkConfig);
            user = new User();
            user.setFirstName(group.getName());
            user.setLastName(group.getType().toString());
        } else
            user = connectToUser(json, eventType, vkConfig);
        if (type.equals("\"photo_comment_delete\"")) {
            s.append("Пользователь: \"").append(user.getFirstName()).append(" ")
                    .append(user.getLastName()).append("\" удалил комментарий под фото: \n")
                    .append(getDeletedCommentUrl(json)).append("\n");
            return s.toString();
        }
        WallComment photoComment = gson.fromJson(json.getAsJsonObject("object"), WallComment.class);
        photoComment.setOwnerId(Integer.valueOf(json.getAsJsonObject("object")
                .getAsJsonPrimitive("photo_owner_id").toString().replaceAll("\"", "")));
        photoComment.setId(Integer.valueOf(json.getAsJsonObject("object")
                .getAsJsonPrimitive("photo_id").toString().replaceAll("\"", "")));
        System.out.println(photoComment.getId());
        switch (type) {
            case "\"photo_comment_new\"":
                s.append("Новый комментарий под фото: ").append(getPhotoCommentUrl(photoComment));
                s.append("Имя комментатора: \"").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\"\n");
                s.append("Текст комментария: \"").append(photoComment.getText()).append("\"\n");
                break;
            case ("\"photo_comment_edit\""):
                s.append("Пользователь: \"").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\" отредактировал комментарий под фото: ")
                        .append("\n").append(getPhotoCommentUrl(photoComment))
                        .append("Текст отредактированного комментария: \"")
                        .append(photoComment.getText()).append("\"\n");
                break;
            case ("\"photo_comment_restore\""):
                s.append("Пользователь: \"").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\"восстановил коментарий под фото: ")
                        .append("\n").append(getPhotoCommentUrl(photoComment))
                        .append("Текст комментария: \"").append(photoComment.getText()).append("\"\n");
                break;
        }
        if (photoComment.getAttachments() != null) {
            s.append(attachmentsParser.parse(json, eventType));
        }
        return s.toString();
    }

    public String getPhotoCommentUrl(WallComment photoComment) {
        return "https://vk.com/public" + vkConfig.getVkGroupId() + "?z=photo" + photoComment.getOwnerId()
                + "_" + photoComment.getId() + "_r" + photoComment.getId() + "\n";
    }

    public String getDeletedCommentUrl(JsonObject jsonObject) {
        return "https://vk.com/public" + vkConfig.getVkGroupId() + "?z=photo" +
                jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("owner_id") + "_"
                + jsonObject.getAsJsonObject("object")
                .getAsJsonPrimitive("photo_id") + "\n";
    }

    public boolean isEventFromGroup(JsonObject json, EventTypes eventTypes) {
        switch (eventTypes) {
            case DELETED_EVENT:
                if (json.getAsJsonObject("object")
                        .getAsJsonPrimitive("deleter_id").toString().startsWith("\"-")) {
                    return true;
                } else
                    return false;
            default:
                if (json.getAsJsonObject("object").getAsJsonPrimitive("from_id").toString().startsWith("\"-")) {
                    return true;
                } else
                    return false;
        }
    }

    public User connectToUser(JsonObject json, EventTypes eventType) throws ClientException, ApiException {
        User user = new User();
        if (!isEventFromGroup(json, eventType)) {
            user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                    .userIds(json.getAsJsonObject("object")
                            .getAsJsonPrimitive("deleter_id").toString())
                    .execute().get(0);
        } else {
            user.setFirstName("Группа контакте");
            user.setLastName("");
        }
        return user;
    }
}
