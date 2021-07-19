package ru.tsoyk.vk.parcers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.board.TopicComment;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tsoyk.tg.models.EventTypes;
import ru.tsoyk.vk.config.VkConfig;

@Service
public class TopicCommentParser implements VkParserInterface {
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
        String topicId = json.getAsJsonObject("object")
                .getAsJsonPrimitive("topic_id").toString().replaceAll("\"", "");
        String topicOwnerId = json.getAsJsonObject("object")
                .getAsJsonPrimitive("topic_owner_id").toString().replaceAll("\"", "");
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
        if (type.contains("delete")) {
            s.append(" Удален комментарий в обсуждении: \n")
                    .append(getDeletedBoardCommentUrl(topicId, topicOwnerId));
            return s.toString();
        }
        TopicComment topicComment = gson.fromJson(json.getAsJsonObject("object"), TopicComment.class);
        switch (type) {
            case "\"board_post_new\"":
                s.append("Новый комментарий в обсуждении: ").append(getBoardCommentUrl(topicComment, topicId, topicOwnerId));
                s.append("Имя комментатора: \"").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\"\n");
                s.append("Текст комментария: \"").append(topicComment.getText()).append("\"\n");
                break;
            case ("\"board_post_edit\""):
                s.append("Пользователь: \"").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\" отредактировал комментарий в обсуждении: ")
                        .append("\n").append(getBoardCommentUrl(topicComment, topicId, topicOwnerId))
                        .append("Текст отредактированного комментария: \"")
                        .append(topicComment.getText()).append("\"\n");
                break;
            case ("\"board_post_restore\""):
                s.append("Пользователь: \"").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\" восстановил коментарий в обсуждении: ")
                        .append("\n").append(getBoardCommentUrl(topicComment, topicId, topicOwnerId))
                        .append("Текст комментария: \"").append(topicComment.getText()).append("\"\n");
                break;
        }
        if (topicComment.getAttachments() != null) {
            s.append(attachmentsParser.parse(json, eventType));
        }
        return s.toString();
    }

    public String getBoardCommentUrl(TopicComment topicComment, String topicId, String topicOwnerId) {
        return "https://vk.com/topic" + topicOwnerId
                + "_" + topicId + "?post=" + topicComment.getId() + "\n";
    }

    public String getDeletedBoardCommentUrl(String topicId, String topicOwnerId) {
        return "https://vk.com/topic" + topicOwnerId
                + "_" + topicId + "\n";
    }
}
