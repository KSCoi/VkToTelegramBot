package ru.tsoyk.service;

import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import org.springframework.stereotype.Service;
import ru.tsoyk.config.VkConfig;

@Service
public class VkMessageParser {

    VkConfig vkConfig;

    public VkMessageParser(VkConfig vkConfig) {
        this.vkConfig = vkConfig;
    }

    public StringBuilder eventJsonParser(JsonObject jsonObject) throws ClientException, ApiException {
        StringBuilder s = new StringBuilder();
         String typeOfEvent = jsonObject.getAsJsonPrimitive("type").toString();
         GetResponse user;
        System.out.println(jsonObject);
        switch (typeOfEvent)  {
            case "\"wall_reply_new\"":
                s.append("Новый комментарий под постом: ").append("https://vk.com/club")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?").append("w=wall").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("post_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("post_id")).append("_r")
                        .append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("id")).append("\n");
                user = vkConfig.getVkApi().users()
                        .get(vkConfig.getActor()).userIds(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("from_id").toString()).execute().get(0);
                s.append("Имя комментатора: " ).append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\n");
                s.append("Текст комментария: ").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"wall_reply_delete\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("deleter_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" удалил комментарий под постом: \n")
                        .append("https://vk.com/club").append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?").append("w=wall").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("owner_id")).append("_")
                        .append(jsonObject.getAsJsonObject("object").getAsJsonPrimitive("post_id"))
                        .append("\n");
                break;
            case("\"wall_reply_edit\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" отредактировал комментарий под постом: ")
                        .append("https://vk.com/club")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?").append("w=wall").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("post_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("post_id")).append("_r")
                        .append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("id")).append("\n")
                        .append("Текст отредактированного комментария: ")
                        .append(jsonObject.getAsJsonObject("object").getAsJsonPrimitive("text"))
                        .append("\n");
                break;
            case("\"wall_reply_restore\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" восстановил коментарий под постом: ")
                        .append("\n").append("https://vk.com/club")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?").append("w=wall").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("post_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("post_id")).append("_r")
                        .append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("id")).append("\n")
                        .append("Текст комментария: ").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"photo_comment_new\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" оставил комментарий под фото: ")
                        .append("https://vk.com/club")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?").append("z=photo").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("photo_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("photo_id")).append("\n")
                        .append("Текст комментария: ")
                        .append(jsonObject.getAsJsonObject("object").getAsJsonPrimitive("text"))
                        .append("\n");
                break;
            case("\"photo_comment_delete\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("deleter_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" удалил комментарий под фото: \n")
                        .append("https://vk.com/club")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?").append("z=photo").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("photo_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("photo_id")).append("\n");
                break;
            case("\"photo_comment_restore\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" восстановил коментарий под фото: ")
                        .append("https://vk.com/club")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?").append("z=photo").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("photo_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("photo_id")).append("\n")
                        .append("Текст комментария: ").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"photo_comment_edit\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" отредактировал комментарий под фото: ")
                        .append("https://vk.com/club")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?").append("z=photo").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("photo_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("photo_id")).append("\n")
                        .append("Текст комментария: ").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"board_post_new\""):
                user = vkConfig.getVkApi().users()
                        .get(vkConfig.getActor()).userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString()).execute().get(0);
                s.append("Новый комментарий в обсуждении: ").append("https://vk.com/topic")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("topic_id"))
                        .append("?post=").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("id")).append("\n");
                s.append("Имя комментатора: " ).append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\n");
                s.append("Текст комментария: ").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"board_post_delete\""):
                s.append(" Удален комментарий в обсуждении: \n")
                        .append("https://vk.com/topic")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("topic_id"))
                        .append("?post=1").append("\n");
                break;
            case("\"board_post_restore\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                    .append(user.getLastName()).append(" восстановил комментарий  в обсуждении: ")
                    .append("https://vk.com/topic")
                    .append(jsonObject.getAsJsonPrimitive("group_id"))
                    .append("_").append(jsonObject.getAsJsonObject("object")
                    .getAsJsonPrimitive("topic_id"))
                    .append("?post=").append(jsonObject.getAsJsonObject("object")
                    .getAsJsonPrimitive("id")).append("\n");
                s.append("Текст комментария: ").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"board_post_edit\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" отредактировал комментарий в обсуждении: ")
                        .append("https://vk.com/topic")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("topic_id"))
                        .append("?post=").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("id")).append("\n");
                s.append("Текст нового комментария: ").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"video_comment_new\""):
                user = vkConfig.getVkApi().users()
                        .get(vkConfig.getActor()).userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString()).execute().get(0);
                s.append("Новый комментарий под видео: ").append("https://vk.com/videos")
                        .append(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("video_owner_id"))
                        .append("?z=video").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("video_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("video_id")).append("\n");
                s.append("Имя комментатора: " ).append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append("\n");
                s.append("Текст комментария: ").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"video_comment_delete\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("deleter_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" Удалил комментарий под видео: \n")
                        .append("https://vk.com/videos")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?z=video").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("video_id")).append("\n");
                break;
            case("\"video_comment_restore\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" восстановил комментарий  под видео: ")
                        .append("https://vk.com/videos")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?z=video").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("video_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("video_id")).append("\n");
                s.append("Текст комментария: ").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"video_comment_edit\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("from_id").toString())
                        .execute().get(0);
                s.append("Пользователь: ").append(user.getFirstName()).append(" ")
                        .append(user.getLastName()).append(" отредактировал комментарий  под видео: ")
                        .append("https://vk.com/videos")
                        .append(jsonObject.getAsJsonPrimitive("group_id"))
                        .append("?z=video").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("video_owner_id"))
                        .append("_").append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("video_id")).append("\n");
                s.append("Текст отредактированного комментария: ")
                        .append(jsonObject.getAsJsonObject("object")
                        .getAsJsonPrimitive("text")).append("\n");
                break;
            case("\"message_new\""):
                user = vkConfig.getVkApi().users().get(vkConfig.getActor())
                        .userIds(jsonObject.getAsJsonObject("object")
                                .getAsJsonPrimitive("user_id").toString())
                        .execute().get(0);
              /*  Message message = vkConfig.getVkApi().messages().getById(vkConfig.getActor(),
                        Integer.valueOf(jsonObject.getAsJsonObject("object").getAsJsonPrimitive("id")
                                .toString().replaceAll("\"",""))).execute().getItems().get(0);
                System.out.println(message);*/

                s.append("Новое личное сообщение от пользователя: ").append(user.getFirstName())
                        .append(" ").append(user.getLastName()).append("\n");
                s.append("По ссылке: ").append("https://vk.com/gim")
                        .append(jsonObject.getAsJsonPrimitive("group_id")).append("?sel=")
                        .append(user.getId()).append("\n");
                s.append("Текст сообщения: ")
                        .append(jsonObject.getAsJsonObject("object").getAsJsonPrimitive("body"))
                        .append("\n");
                /*JsonObject object = jsonObject.getAsJsonObject("object")
                        .getAsJsonArray("fwd_messages").get(0).getAsJsonObject();
                while (object!=null)    {
                    s.append()
                }*/
                break;
            default:
                s.append("UNKNOWN TYPE");
        }
        if(!jsonObject.getAsJsonObject("object").toString().contains("\"fwd_messages\"")
                && jsonObject.getAsJsonObject("object").toString().contains("\"attachments\""))  {

            JsonObject attachmentsJsonObject = jsonObject.getAsJsonObject("object").getAsJsonArray("attachments")
                    .get(0).getAsJsonObject();
            String typeOfAttachments = attachmentsJsonObject.getAsJsonPrimitive("type").toString();
            switch (typeOfAttachments){
                case("\"photo\""):
                    s.append("В сообщении приложена фотография").append("\n")
                            .append(attachmentsJsonObject.getAsJsonObject("photo")
                            .getAsJsonPrimitive("photo_604").toString().replaceAll("\"",""));
                    break;
                case("\"video\""):
                    s.append("В сообщении приложено видео").append("\n");
                    break;
                case("\"doc\""):
                    s.append("В сообщении приложен документ\n")
                            .append(attachmentsJsonObject.getAsJsonObject("doc").getAsJsonPrimitive("url"));
                    break;
                case("\"link\""):
                    s.append("когда вообще это сработает?");
                    break;
            }
        }
       return s;
    }
}
