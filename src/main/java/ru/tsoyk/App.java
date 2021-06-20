package ru.tsoyk;

//import org.springframework.boot.SpringApplication;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import org.springframework.stereotype.Component;

import ru.tsoyk.config.TelegramBotInitializer;


// Аннотация, которая объединяет в себя @Configuration, @EnableAutoConfiguration, @ComponentScan
    @SpringBootApplication()
    @Component
    @Slf4j
    //@AutoConfigurationPackage
    public class App extends SpringBootServletInitializer {

        public static void main(String[] args) throws InterruptedException {
            new SpringApplicationBuilder(App.class)
                   .run(args);
            System.out.println("SERVER RUNNING");
            while (true)    {
                try {
                    Thread.sleep(500);
                    TelegramBotInitializer.bot.sendMsgToChannelWithoutUpdate("-1001435977133");

                }
                catch (NullPointerException  e) {
                }
                catch ( ClientException | ApiException exception)   {
                    for (StackTraceElement element:exception.getStackTrace()) {
                        log.info(element.toString());
                    }

                }

            }
        }
    }

