package ru.tsoyk;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Component;
import ru.tsoyk.tg.init.TelegramBotInitializer;


// Аннотация, которая объединяет в себя @Configuration, @EnableAutoConfiguration, @ComponentScan
@SpringBootApplication()
@Component
@Slf4j
public class App extends SpringBootServletInitializer {
    @Autowired
    static TelegramBotInitializer tgInit;

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(App.class)
                .run(args);
        log.info("SERVER IS RUNNING!");
        while (true) {
            try {
                Thread.sleep(1000);
                tgInit.bot.sendMsgToChannelWithoutUpdate("-1001435977133");
            } catch (NullPointerException e) {
                log.warn("nullpointer", e);
            } catch (ClientException | ApiException exception) {
                log.error("VkApi exception", exception);
                if(exception instanceof ApiException)
                    log.error(((ApiException) exception).getDescription());
            }
        }
        // new App().runApp();

    }

   /* @PostConstruct
        public void runApp() throws InterruptedException {
            while (true)    {
                try {
                    Thread.sleep(500);
                    Thread thread = new Thread();
                    thread.start();
                    bot.sendMsgToChannelWithoutUpdate("-1001435977133");
                }
                catch (NullPointerException  e) {
                    //log.error("nullpointer",e);
                }
                catch ( ClientException | ApiException exception)   {
                    log.error("VkApi exception",exception);
                }
            }
        }*/
}

