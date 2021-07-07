package ru.tsoyk.vk.parcers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.audio.Audio;
import com.vk.api.sdk.objects.messages.AudioMessage;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import com.vk.api.sdk.objects.messages.MessageAttachmentType;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk.api.sdk.objects.video.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tsoyk.tg.models.EventTypes;
import ru.tsoyk.vk.config.VkConfig;

@Service
public class AttachmentsParser implements VkParserInterface {
    @Autowired
    VkConfig vkConfig;

    public String parse(JsonObject json, EventTypes eventType) throws ClientException, ApiException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        StringBuilder s = new StringBuilder("");
        MessageAttachment[] attachments = gson.fromJson(json.getAsJsonObject("object")
                .getAsJsonArray("attachments"), MessageAttachment[].class);
        for (MessageAttachment attachment : attachments) {
            s.append("Приложен объект: ");
            System.out.println(attachment);
            if (attachment.getType().equals(MessageAttachmentType.PHOTO)) {
                s.append("фотография").append("\n");
                int position = attachment.getPhoto().getSizes().size();
                PhotoSizes image = attachment.getPhoto().getSizes().get(position - 1);
                s.append(image.getUrl()).append("\n");
            } else if (attachment.getType().equals(MessageAttachmentType.VIDEO)) {
                Video video = attachment.getVideo();
                s.append("видео").append("\n");
                s.append("Название видео: ").append(video.getTitle()).append("\n");
                s.append("Описание видео: \" ").append(video.getDescription()).append("\"\n");
            } else if (attachment.getType().equals(MessageAttachmentType.AUDIO)) {
                Audio audio = attachment.getAudio();
                s.append("аудио").append("\n");
                s.append("название аудио: \" ").append(audio.getTitle()).append("\"\n");
            } else if (attachment.getType().equals(MessageAttachmentType.AUDIO_MESSAGE)) {
                AudioMessage audioMessage = attachment.getAudioMessage();
                s.append("аудиосообщение: ").append(audioMessage.getLinkMp3()).append("\n");
            } else s.append(" медиаконтент").append("\n");
        }
        return s.toString();
    }
}
