package ru.tsoyk;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tsoyk.tg.init.TelegramBotInitializer;

@SpringBootApplication()
@Component
@Slf4j
@EnableScheduling
public class App extends SpringBootServletInitializer {
    @Autowired
    static TelegramBotInitializer tgInit;

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(App.class)
                .run(args);
        log.info("SERVER IS RUNNING!");
            try {
                new App().sendMessage();
            } catch (NullPointerException e) {
                log.warn("nullpointer", e);
            } catch (ClientException | ApiException exception) {
                log.error("VkApi exception", exception);
                if (exception instanceof ApiException)
                    log.error(((ApiException) exception).getDescription());
            }

    }
    @Scheduled(fixedRate = 1000)
    public void sendMessage() throws ClientException, ApiException {
        tgInit.bot.sendMsgToChannelWithoutUpdate("-1001435977133");
    }
}

