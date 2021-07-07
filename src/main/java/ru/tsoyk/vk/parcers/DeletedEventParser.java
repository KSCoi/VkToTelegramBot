package ru.tsoyk.vk.parcers;

import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.springframework.stereotype.Service;
import ru.tsoyk.tg.models.EventTypes;

@Service
public class DeletedEventParser implements VkParserInterface{

    @Override
    public String parse(JsonObject json, EventTypes eventType) throws ClientException, ApiException {
        StringBuilder s = new StringBuilder("");
       if(json.getAsJsonObject("type").toString().contains("")){

       }
        return s.toString();
    }
}
