package ru.tsoyk.vk.parcers;

import com.google.gson.JsonObject;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.Validable;

public interface VkParserInterface extends Validable {

    String parse(JsonObject json) throws ClientException, ApiException;
}
