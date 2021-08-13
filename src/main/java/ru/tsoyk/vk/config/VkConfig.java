package ru.tsoyk.vk.config;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@PropertySource("classpath:application.properties")
public class VkConfig {

    private final Integer vkGroupId;

    private final String vkToken;

    public static int ts;

    VkConfig(@Value("${access_token}") String vkToken, @Value("${vkGroupId}") Integer vkGroupId) {
        this.vkToken = vkToken;
        this.vkGroupId = vkGroupId;
    }

    @Bean
    public VkApiClient getVkApi() {
        return new VkApiClient(HttpTransportClient.getInstance());
    }

    @Bean
    public GroupActor getActor() {
        return new GroupActor(vkGroupId, vkToken);
    }

}
