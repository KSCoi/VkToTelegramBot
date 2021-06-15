package ru.tsoyk.config;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;

@Configuration
@Getter
@PropertySource("classpath:application.properties")
public class VkConfig {

    final Integer vkGroupId;

    final String vkToken;

    public static int ts;

    public static int maxMsgId=-1;


    VkConfig(@Value("${access_token}") String vkToken, @Value("${vkGroupId}")Integer vkGroupId) {
        this.vkToken = vkToken;
        this.vkGroupId = vkGroupId;
    }
    @Bean
    public VkApiClient getVkApi()  {
        return new VkApiClient(HttpTransportClient.getInstance());
    }


    @Bean
    public GroupActor getActor()   {
        return new GroupActor(vkGroupId,vkToken);
    }



}
