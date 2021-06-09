package ru.tsoyk.config;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.GroupAuthResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@Data
@PropertySource("classpath:application.properties")
public class VkInitLongPoll {

    @Value("${group_id}")
    final Integer APP_ID;

    @Value("${access_token")
    final String accessToken;

    @Value("${client_secret}")
    final String CLIENT_SECRET;

    @Value("${URI}")
    final String REDIRECT_URI;

    @Value("${code}")
    String code;

    private static int ts;


    TransportClient transportClient = new HttpTransportClient();
    VkApiClient vkApiClient = new VkApiClient(transportClient);
    GroupActor groupActor = new GroupActor(APP_ID,accessToken);

    public static void main(String[] args) {
        
    }


}
