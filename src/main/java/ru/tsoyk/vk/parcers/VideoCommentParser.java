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
public class VideoCommentParser implements VkParserInterface {
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
        User user;
        Group group;
        if (isEventFromGroup(json, eventType)) {
            group = connectToGroup(json, eventType, vkConfig);
            if(group.getId().equals(vkConfig.getVkGroupId()))
                return "";
            user = new User();
            user.setFirstName(group.getName());
            user.setLastName(group.getType().toString());
        } else
            user = connectToUser(json, eventType, vkConfig);

        if (type.contains("_delete")) {
            s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                    .append(user.getLastName()).append(" Удалил комментарий под видео: \n")
                    .append(getDeleteVideoCommentUrl(json));
            return s.toString();
        }
        WallComment videoComment = gson.fromJson(json.getAsJsonObject("object"), WallComment.class);
        videoComment.setOwnerId(Integer.valueOf(json.getAsJsonObject("object")
                .getAsJsonPrimitive("video_owner_id").toString().replaceAll("\"", "")));
        videoComment.setId(Integer.valueOf(json.getAsJsonObject("object")
                .getAsJsonPrimitive("video_id").toString().replaceAll("\"", "")));
        switch (type) {
            case "\"video_comment_new\"":
                s.append("Новый комментарий под видео: ").append(getPhotoCommentUrl(videoComment));
                s.append("Имя комментатора:\" ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\"\n");
                s.append("Текст комментария:\" ").append(videoComment.getText()).append("\"\n");
                break;
            case ("\"video_comment_edit\""):
                s.append("Пользователь:\" ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\" отредактировал комментарий под видео: ")
                        .append("\n").append(getPhotoCommentUrl(videoComment))
                        .append("Текст отредактированного комментария:\" ")
                        .append(videoComment.getText()).append("\"\n");
                break;
            case ("\"video_comment_restore\""):
                s.append("Пользователь:\" ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\" восстановил коментарий под видео: ")
                        .append("\n").append(getPhotoCommentUrl(videoComment))
                        .append("Текст комментария:\" ").append(videoComment.getText()).append("\"\n");
                break;
        }
        if (videoComment.getAttachments() != null) {
            s.append(attachmentsParser.parse(json, eventType));
        }
        return s.toString();
    }

    public String getPhotoCommentUrl(WallComment wallComment) {
        return "https://vk.com/videos" + vkConfig.getVkGroupId() * -1
                + "?z=video" + wallComment.getOwnerId() + "_" + wallComment.getId() + "\n";
    }

    public String getDeleteVideoCommentUrl(JsonObject jsonObject) {
        return "https://vk.com/videos-" + jsonObject.getAsJsonPrimitive("group_id") + "?z=video"
                + jsonObject.getAsJsonObject("object")
                .getAsJsonPrimitive("owner_id") + "_" + jsonObject.getAsJsonObject("object")
                .getAsJsonPrimitive("video_id") + "\n";
    }
}
