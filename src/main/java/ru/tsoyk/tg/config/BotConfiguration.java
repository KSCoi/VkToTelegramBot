package ru.tsoyk.tg.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Getter
@Setter
@PropertySource("classpath:application.properties")
public class BotConfiguration {

    @Value("${botUserName}")
   private String botUserName;

    @Value("${token}")
   private String token;
}
