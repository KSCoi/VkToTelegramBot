package ru.tsoyk.vk.parcers;

import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.Validable;
import com.vk.api.sdk.objects.groups.Group;
import com.vk.api.sdk.objects.users.User;
import ru.tsoyk.tg.models.EventTypes;
import ru.tsoyk.vk.config.VkConfig;

public interface VkParserInterface extends Validable {

    String parse(JsonObject json, EventTypes eventType) throws ClientException, ApiException;

    default boolean isEventFromGroup(JsonObject json, EventTypes eventTypes) {
        switch (eventTypes) {
            case DELETED_EVENT:
                if (json.getAsJsonObject("object")
                        .getAsJsonPrimitive("deleter_id").toString().startsWith("-")) {
                    return true;
                } else
                    return false;
            default:
                if (json.getAsJsonObject("object").getAsJsonPrimitive("from_id").toString().startsWith("-")) {
                    return true;
                } else
                    return false;
        }
    }

    default User connectToUser(JsonObject json, EventTypes eventType, VkConfig vkConfig) throws ClientException, ApiException {
        User user;
        switch (eventType) {
            case DELETED_EVENT:
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(json.getAsJsonObject("object")
                                .getAsJsonPrimitive("deleter_id").toString())
                        .execute().get(0);
                return user;
            default:
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(json.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                return user;
        }
    }

    default Group connectToGroup(JsonObject json, EventTypes eventType, VkConfig vkConfig) throws ClientException, ApiException {
        Group group;
        switch (eventType) {
            case DELETED_EVENT:
                group = vkConfig.getVkApi().groups().getByIdLegacy(vkConfig.getActor()).groupId(json.getAsJsonObject("object")
                        .getAsJsonPrimitive("deleter_id").toString())
                        .execute().get(0);
                return group;
            default:
                group = vkConfig.getVkApi().groups().getByIdLegacy(vkConfig.getActor()).groupId(json.getAsJsonObject("object")
                        .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                return group;
        }
    }
}
